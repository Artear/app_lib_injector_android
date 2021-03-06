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

import com.artear.injector.api.JsEventManager
import com.artear.injector.api.JsInterface
import com.artear.injector.compiler.process.JsEventManagerProcess
import com.artear.injector.compiler.process.JsInterfaceProcess
import com.artear.injector.compiler.util.executeProcess
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * This is the start point of this library.
 *
 * There is a file, in resources folder, META-INF.services, called Processor that you must to
 * set this class for the annotation processor take your class and init the routine.
 *
 * The main process is [executeProcess].
 *
 * *IMPORTANT*: this a beta version of how create classes with an annotation processor. If any class
 * is modify in [WebWrapper](https://github.com/Artear/app_lib_webwrap_android) maybe you must
 * change some generated code classes in this library.
 *
 * @author David Tolchinsky
 */
class ArtearProcessor : AbstractProcessor() {

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    /**
     * Configuration for generate code across kapt
     */
    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(Config.KAPT_KOTLIN_GENERATED_OPTION_NAME)
    }

    /**
     * Here you must to put all annotation that you want process to generate an additional
     * functionality.
     */
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(JsInterface::class.java.canonicalName,
                JsEventManager::class.java.canonicalName)
    }

    /**
     * The main function to process the recollected data of your application.
     */
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val jsInterfaceProcess = JsInterfaceProcess(processingEnv)
        val jsEventManagerProcess = JsEventManagerProcess(processingEnv, jsInterfaceProcess)
        val processList = setOf(jsInterfaceProcess, jsEventManagerProcess)
        processList.forEach { roundEnv.executeProcess(it) }
        return true
    }

}


