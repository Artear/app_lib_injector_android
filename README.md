# Injector

Injector Library

- This is a beta library used to generate some little useful classes in for 
Artear projects, particularly for [WebWrapper](https://github.com/Artear/app_lib_webwrap_android). 
Maybe you can catch any idea or simplely add extra functionality for 
your application.

 
Documentation
--------

This library has two modules and export two different .jar and generate
independent docs on the same folder.

On the one hand injector-api only has the annotations classes to 
process. On the other the injector-compiler use the injector-api 
to generate the files across the annotation processor. Both use the same 
gradle for publication and distribution.

The start point is [ArtearProcessor](./injector-compiler/src/main/java/com/artear/injector/compiler/ArtearProcessor.kt).
This process all classes of your project that use [JsInterface](./injector-api/src/main/java/com/artear/injector/api/JsInterface.kt)
creating one extra file for each one. The result is a useful way to 
control all javascript event in your WebView. Each event has an object
associated and a unique responsibility. 

For a friendship way to generate code use a [Process](./injector-compiler/src/main/java/com/artear/injector/compiler/process/Process.kt)
and the idea to encapsulate each annotation for process them. Create a 
model class like [JsInterfaceClass](./injector-compiler/src/main/java/com/artear/injector/compiler/process/model/JsInterfaceClass.kt) 
and use this in a process to generate each **object** to help you for the
creation of file. 

That object is a [kotlin poet](https://github.com/square/kotlinpoet) 
object, like FunSpec, TypeSpec. This library is a very good Kotlin API 
for generating .kt source files.


*See entire documentation library [here](https://artear.github.io/app_lib_injector_android).*

 
Implementation
--------

lastVersion = 0.0.6

Gradle:
```groovy
implementation "com.artear.injector:injector-api:$lastVersion"
kapt "com.artear.injector:injector-compiler:$lastVersion"
```

License
=======

    Copyright 2019 Artear S.A.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
