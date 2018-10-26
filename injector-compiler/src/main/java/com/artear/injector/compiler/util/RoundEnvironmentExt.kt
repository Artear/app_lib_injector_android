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

fun <T> RoundEnvironment.executeProcess(process: Process<T>) {
    getElementsAnnotatedWith(process.annotation)
            .filterIsInstance<TypeElement>()
            .filter { Utils.isValidClass(it, process.messager) }
            .map { process.buildAnnotationClass(it) }
            .forEach { process.createAnnotationFile(it) }

}