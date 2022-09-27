package io.grule.util.log

fun interface Printer {
    fun print(level: LogLevel, tag: String, message: Any?)
}