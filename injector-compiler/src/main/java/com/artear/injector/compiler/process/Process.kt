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

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Is a simple class to model each annotation to process.
 * Extend them if need a new annotation flow process.
 *
 */
abstract class Process<T>(val processingEnv: ProcessingEnvironment) {

    /**
     * Useful to print console message.
     */
    val messager: Messager = processingEnv.messager

    /**
     * Useful to extract package name.
     *
     * See: [TypeElement]
     */
    val elements: Elements = processingEnv.elementUtils

    /**
     * Useful to get TypeElement from TypeMirror
     *
     * See: [TypeElement]
     */
    val typeUtils: Types = processingEnv.typeUtils

    /**
     * The annotation to process.
     */
    abstract val annotation: Class<out Annotation>

    /**
     * Create an annotation class model.
     */
    abstract fun buildAnnotationClass(typeElement: TypeElement): T

    /**
     * Use for make a file and extend the functionality of that [annotationClass].
     */
    abstract fun createAnnotationFile(annotationClass: T)
}