/*
 * Copyright 2018 Artear S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artear.injector.compiler.process

import com.artear.injector.api.JsInterface
import com.artear.injector.compiler.ArtearGenerator
import com.artear.injector.compiler.process.model.JsInterfaceClass
import com.artear.injector.compiler.util.KotlinFiler
import com.artear.injector.compiler.util.Utils
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror


class JsInterfaceProcess(processingEnv: ProcessingEnvironment) : Process<JsInterfaceClass>(processingEnv) {

    override val annotation: Class<out Annotation> = JsInterface::class.java

    val jsInterfaceClassList = mutableListOf<JsInterfaceClass>()

    override fun buildAnnotationClass(typeElement: TypeElement): JsInterfaceClass {
        val jsInterfaceClass = buildAnnotationClassBase(typeElement)
        jsInterfaceClassList.add(jsInterfaceClass)
        return jsInterfaceClass
    }

    private fun buildAnnotationClassBase(typeElement: TypeElement): JsInterfaceClass {
        val packageName = Utils.packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        val unsafeKey = Utils.findAnnotationValue(typeElement, JsInterface::class.qualifiedName,
                "key", String::class.java)
        val key = checkNotNull(unsafeKey) { "The key can not be null on JsInterface annotation" }
        val interfacePairType = typeElement.interfaces
                .map { return@map getPairType(it) }
                .first()

        return JsInterfaceClass(packageName, className, key, interfacePairType)
    }

    private fun getPairType(it: TypeMirror): Pair<String, String> {
        val element = typeUtils.asElement(it) as TypeElement
        var typeInterface: String? = null
        if (it is DeclaredType) {
            typeInterface = it.typeArguments.first().toString()
        }
        checkNotNull(typeInterface) {
            "Can not be null the type interface"
        }
        return Pair(element.qualifiedName.toString().substringAfterLast("."), typeInterface)
    }

    override fun createAnnotationFile(annotationClass: JsInterfaceClass) {
        val jsInterfaceTypeSpec = ArtearGenerator.generateJsInterfaceTypeSpec(annotationClass)
        val file = FileSpec.builder(annotationClass.packageName, jsInterfaceTypeSpec.name!!)
                .addType(jsInterfaceTypeSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())
    }


}