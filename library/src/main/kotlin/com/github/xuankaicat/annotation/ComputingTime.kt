package com.github.xuankaicat.annotation

import com.github.xuankaicat.processor.ComputingTimeProcessor

@Target(AnnotationTarget.FUNCTION)
@Processor(ComputingTimeProcessor::class)
annotation class ComputingTime
