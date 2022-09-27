package io.grule.util.log

internal object LoggerEmpty : Logger {

    override fun level(level: LogLevel): Logger = this

    override fun tag(tag: String): Logger = this

    override fun printer(printer: Printer): Logger = this

    override fun invoke(fn: () -> Any?): Logger = this
}