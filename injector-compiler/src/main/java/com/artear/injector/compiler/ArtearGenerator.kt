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
package com.artear.injector.compiler

import com.artear.injector.compiler.process.model.JsEventManagerClass
import com.artear.injector.compiler.process.model.JsInterfaceClass
import com.squareup.kotlinpoet.*

/**
 * This generator works like a helper to make the necessary poet objects for all process.
 *
 * Like -> JsInterfaceProcess and JsEventManagerProcess
 *
 * @see [Taggable] extensions in koltin poet
 */
internal object ArtearGenerator {

    /**
     * This suffix will be added to each class that have an [JsInterface] annotation
     */
    private const val CLASS_NAME_JS_SUFFIX = "Js"

    /**
     * Generate a handy object [TypeSpec] with [jsInterfaceClass] model.
     * Based on [WebWrapp](https://github.com/Artear/app_lib_webwrap_android) library.
     */
    fun generateJsInterfaceTypeSpec(jsInterfaceClass: JsInterfaceClass): TypeSpec {

        val builder = TypeSpec.classBuilder(jsInterfaceClass.className + CLASS_NAME_JS_SUFFIX)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        val upPackageName = "com.artear.webwrap.presentation.webjs"
        val commandClassName = ClassName(upPackageName, "CommandJs")

        builder.addSuperinterface(commandClassName)

        val contextClassName = ClassName("android.content", "Context").copy(nullable = true)
        val contextNameParam = "context"
        val contextParam = ParameterSpec.builder(contextNameParam, contextClassName,
                KModifier.OVERRIDE).build()
        val contextProperty = PropertySpec.builder(contextNameParam, contextClassName)
                .initializer(contextNameParam)
                .mutable(true)
                .build()

        val targetClassName = ClassName(jsInterfaceClass.packageName, jsInterfaceClass.className)
        val targetNameParam = targetClassName.simpleName.toLowerCase()
        val targetParam = ParameterSpec.builder(targetNameParam,
                targetClassName, KModifier.PRIVATE)
                .build()
        val targetProperty = PropertySpec.builder(targetNameParam, targetClassName)
                .initializer(targetNameParam)
                .build()

        val delegateClassName = ClassName(upPackageName, "WebJsDispatcher").copy(nullable = true)
        val delegateNameParam = "delegate"
        val delegateParam = ParameterSpec.builder(delegateNameParam, delegateClassName,
                KModifier.OVERRIDE)
                .build()
        val delegateProperty = PropertySpec.builder(delegateNameParam, delegateClassName)
                .initializer(delegateNameParam)
                .mutable(true)
                .build()

        val keyProperty = PropertySpec.builder("key", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", jsInterfaceClass.key)
                .build()

        builder.primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(contextParam)
                .addParameter(targetParam)
                .addParameter(delegateParam)
                .build())
                .addProperty(contextProperty)
                .addProperty(targetProperty)
                .addProperty(delegateProperty)
                .addProperty(keyProperty)

        val indexNameParam = "index"
        val indexParam = ParameterSpec.builder(indexNameParam, Int::class).build()
        val dataJsonParam = ParameterSpec.builder("dataJson", String::class).build()

        val javaScriptClassName = ClassName("android.webkit", "JavascriptInterface")
        val suppressClassName = ClassName("android.annotation", "SuppressLint")
        val suppressAnnotation = AnnotationSpec.builder(suppressClassName)
                .addMember("%S", "CheckResult")
                .build()

        val nameInterfaceGenericType = jsInterfaceClass.interfaceType.second.substringAfterLast(".")

        val classNameJsonAdapter = ClassName(jsInterfaceClass.packageName, "${nameInterfaceGenericType}JsonAdapter")
        val classNameMoshi = ClassName("com.squareup.moshi", "Moshi")

        val classNameDispatch = ClassName(upPackageName, "dispatch")
        val classNameError = ClassName(upPackageName, "error")


        val codeBuilder = CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("val adapter = %T(%T.Builder().build())", classNameJsonAdapter,
                        classNameMoshi)
                .addStatement("val data = adapter.fromJson(dataJson)")

        when (jsInterfaceClass.interfaceType.first) {
            "SyncEventJs" -> {
                codeBuilder.addStatement("val event = $targetNameParam.event(" +
                        "$contextNameParam!!,$indexNameParam, data!!)")
                codeBuilder.addStatement("$delegateNameParam!!.%T(event)", classNameDispatch)
            }
            "DeferEventJs" -> {
                codeBuilder.addStatement("$targetNameParam.event(" +
                        "$contextNameParam!!, $delegateNameParam!!, $indexNameParam, data!!)")
            }
        }

        val executeCode = codeBuilder.nextControlFlow("catch (ex: %T)", Exception::class)
                .addStatement("$delegateNameParam?.%T($indexNameParam, ex.message)", classNameError)
                .endControlFlow()
                .build()

        val executeFunction = FunSpec.builder("execute")
                .addAnnotation(suppressAnnotation)
                .addAnnotation(javaScriptClassName)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(indexParam)
                .addParameter(dataJsonParam)
                .addCode(executeCode)
                .build()

        builder.addFunction(executeFunction)

        return builder.build()
    }

    /**
     * Generate a handy object [FunSpec] with [JsEventManagerClass] model.
     * Based on [WebWrapp](https://github.com/Artear/app_lib_webwrap_android) library.
     */
    fun generateJsEventManagerTypeSpec(annotationClass: JsEventManagerClass,
                                       jsInterfaceClassList: MutableList<JsInterfaceClass>): FunSpec {

        val className = ClassName(annotationClass.packageName, annotationClass.className)
        val builder = FunSpec.builder("initialize")
                .receiver(className)
        builder.returns(className)
                .addModifiers(KModifier.INTERNAL)

        val webViewClassName = ClassName("android.webkit", "WebView")
        val webViewParam = ParameterSpec.builder("it", webViewClassName).build()

        builder.addParameter(webViewParam)
                .addStatement("webView = it")

        val delegateClassName = ClassName(annotationClass.packageName, "WebJsDispatcher")
        builder.addStatement("val delegate : %T = { executeJS(it) }", delegateClassName)

        jsInterfaceClassList.forEach {
            val eventJsClassName = ClassName(it.packageName, it.className)
            val commandJsClassName = ClassName(it.packageName, it.className + CLASS_NAME_JS_SUFFIX)
            builder.addStatement("commands.add(%T(it.context, %T(), delegate))",
                    commandJsClassName, eventJsClassName)
        }

        builder.addStatement("addJavascriptInterfaces(it)")
        builder.addStatement("return this")

        return builder.build()
    }

    /**
     * Generate a handy object [FunSpec] with [JsEventManagerClass] model for make an extension
     * to WebWrapper
     * Based on [WebWrapp](https://github.com/Artear/app_lib_webwrap_android) library.
     */
    fun generateWebWrapperTypeSpec(annotationClass: JsEventManagerClass): FunSpec {
        val className = ClassName("com.artear.webwrap", "WebWrapper")
        //TODO add all project classes and all webwrap classes
        val builder = FunSpec.builder("loadAllJsInterface")
                .receiver(className)
                .addModifiers(KModifier.PUBLIC)

        val webJsEventManagerClassName = ClassName(annotationClass.packageName,
                annotationClass.className)
        val webJsEventManagerParam = ParameterSpec.builder(annotationClass.className.decapitalize(),
                webJsEventManagerClassName).build()

        val initializeClassName = ClassName(annotationClass.packageName, "initialize")
        val webWrapperCode = CodeBlock.builder()
                .beginControlFlow("webView?.let")
                .addStatement("this.webJsEventManager = webJsEventManager.%T(it)", initializeClassName)
                .endControlFlow()
                .build()

        builder.addParameter(webJsEventManagerParam)
                .addCode(webWrapperCode)

        return builder.build()
    }
}