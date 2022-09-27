package io.grule.util.log

interface LoggerBuilder {

    fun level(level: LogLevel): LoggerBuilder

    val verbose: LoggerBuilder get() = level(LogLevel.VERBOSE)

    val debug: LoggerBuilder get() = level(LogLevel.DEBUG)

    val info: LoggerBuilder get() = level(LogLevel.INFO)

    val warn: LoggerBuilder get() = level(LogLevel.WARN)

    val error: LoggerBuilder get() = level(LogLevel.ERROR)

    fun tag(tag: String): LoggerBuilder

    fun printer(printer: Printer): LoggerBuilder

    fun build(): Logger
}