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


class ArtearProcessor : AbstractProcessor() {

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(Config.KAPT_KOTLIN_GENERATED_OPTION_NAME)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(JsInterface::class.java.canonicalName,
                JsEventManager::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val jsInterfaceProcess = JsInterfaceProcess(processingEnv)
        val jsEventManagerProcess = JsEventManagerProcess(processingEnv, jsInterfaceProcess)
        val processList = setOf(jsInterfaceProcess, jsEventManagerProcess)
        processList.forEach { roundEnv.executeProcess(it) }
        return true
    }

}


