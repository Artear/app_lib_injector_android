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

import com.artear.injector.compiler.process.Process
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * Function extension of RoundEnvironment that execute a [Process].
 *
 * Execute in this order.
 *
 * 1 - Get the Elements for the annotation class.
 *
 * 2/3 - Filter by [TypeElement] instance and then filter by is [isValidClass].
 *
 * 4 - Map each element and transform in annotation class model.
 *
 * 5 - Use that model to create a file that extends functionality
 *
 */
fun <T> RoundEnvironment.executeProcess(process: Process<T>) {
    getElementsAnnotatedWith(process.annotation)
            .filterIsInstance<TypeElement>()
            .filter { it.isValidClass(process.messager) }
            .map { process.buildAnnotationClass(it) }
            .forEach { process.createAnnotationFile(it) }

}