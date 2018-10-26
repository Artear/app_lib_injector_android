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


abstract class Process<T>(val processingEnv: ProcessingEnvironment) {

    val messager: Messager = processingEnv.messager
    val elements: Elements = processingEnv.elementUtils
    val typeUtils: Types = processingEnv.typeUtils

    abstract val annotation: Class<out Annotation>

    abstract fun buildAnnotationClass(typeElement: TypeElement): T

    abstract fun createAnnotationFile(annotationClass: T)
}