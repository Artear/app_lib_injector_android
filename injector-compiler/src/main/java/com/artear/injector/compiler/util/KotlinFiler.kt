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
package com.artear.injector.compiler.util

import com.artear.injector.compiler.Config
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

/**
 * The Kotlin Filer is used to make a file in an specific directory
 */
class KotlinFiler(processingEnv: ProcessingEnvironment) {

    /**
     * The singleton to retain this Filer.
     */
    companion object : SingletonHolder<KotlinFiler, ProcessingEnvironment>(::KotlinFiler)

    /**
     * Dynamic directory to generate all files. Commonly the main package name of each project.
     *
     */
    private val kaptKotlinGeneratedDir: String =
            processingEnv.options[Config.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can't find the target directory for generated Kotlin files.")
                throw KotlinFilerException("Can't generate Kotlin files.")
            }

    /**
     * Create a new file in [kaptKotlinGeneratedDir]
     */
    fun newFile(): File {
        return File(kaptKotlinGeneratedDir)
    }

}