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
package com.artear.injector.compiler.process.model

/**
 * @param packageName The original package name of the class that have "com.artear.annotations.JsInterface"
 * @param className The name of the class
 * @param key The key word of annotation
 * @param interfaceType Its a pair, first is the type interface, second is the
 * generic type of that interface type. For example: List<Int> -> first is List, and second
 * is Int
 */
class JsInterfaceClass(val packageName: String, val className: String, val key: String,
                       val interfaceType: Pair<String, String>)