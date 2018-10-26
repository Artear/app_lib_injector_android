package com.artear.injector.api

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class JsInterface(val key: String)