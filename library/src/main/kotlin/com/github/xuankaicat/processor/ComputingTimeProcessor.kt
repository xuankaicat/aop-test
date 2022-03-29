package com.github.xuankaicat.processor

import kotlin.reflect.KFunction

class ComputingTimeProcessor<R>(
    private val function: KFunction<R>,
    private vararg val args: Any?
) {
    private var startTime: Long = 0
    private var endTime: Long = 0

    operator fun invoke() {
        startTime = System.currentTimeMillis()
        function.call(*args)
        endTime = System.currentTimeMillis()

        println("Computing time: ${getTime()} ms")
    }

    fun getTime(): Long {
        return endTime - startTime
    }
}