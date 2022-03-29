package com.github.xuankaicat.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class Processor(val processor: KClass<*>)
