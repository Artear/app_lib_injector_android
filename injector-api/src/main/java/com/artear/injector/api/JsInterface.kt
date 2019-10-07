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
package com.artear.injector.api

/**
 * Use them to establish a suitable communication between a single javascript event for your web
 * loaded in a webview and an object in your application, making a clean code and a single
 * responsibility.
 *
 * The ArtearProcessor use this annotation to generate an extra file for execute a traditional
 * JavascriptInterface used in webview.
 *
 * Note that the process register each class annotated whit it.
 *
 * @param key the javascript event name
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class JsInterface(val key: String)