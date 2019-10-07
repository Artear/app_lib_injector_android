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

import com.artear.injector.api.JsEventManager
import com.artear.injector.compiler.ArtearGenerator
import com.artear.injector.compiler.process.model.JsEventManagerClass
import com.artear.injector.compiler.util.KotlinFiler
import com.artear.injector.compiler.util.Utils
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement


/**
 * Use JsEventManager and process them.
 *
 * The aim is extend a simple function for initialize them and add each JsInterface like a new
 * command. Also use a function addJavascriptInterfaces to attach the js flow events between the
 * webview and that manager.
 *
 * See: WebWrap [library](https://github.com/Artear/app_lib_webwrap_android) - WebJsEventManager
 */
class JsEventManagerProcess(processingEnv: ProcessingEnvironment,
                            private val jsInterfaceProcess: JsInterfaceProcess) :
        Process<JsEventManagerClass>(processingEnv) {

    /**
     * The annotation to process: JsEventManager
     */
    override val annotation: Class<out Annotation> = JsEventManager::class.java

    /**
     * Given a [TypeElement], get the package name and class name and instance a new model.
     *
     * @return A [JsEventManagerClass] model
     */
    override fun buildAnnotationClass(typeElement: TypeElement): JsEventManagerClass {
        val packageName = Utils.packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        return JsEventManagerClass(packageName, className)
    }

    /**
     * Make two files, one extended an event manager and another extended the WebWrapper
     * This second extension use the first to initialize and load all js interfaces.
     */
    override fun createAnnotationFile(annotationClass: JsEventManagerClass) {
        val jsEventManagerFuncSpec = ArtearGenerator.generateJsEventManagerTypeSpec(
                annotationClass, jsInterfaceProcess.jsInterfaceClassList)
        var file = FileSpec.builder(annotationClass.packageName, "${annotationClass.className}Ext")
                .addFunction(jsEventManagerFuncSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())

        val webWrapperFuncSpec = ArtearGenerator.generateWebWrapperTypeSpec(annotationClass)
        file = FileSpec.builder("com.artear.webwrap", "WebWrapperExt")
                .addFunction(webWrapperFuncSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())
    }

}
