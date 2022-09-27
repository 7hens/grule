package io.grule.util.log

abstract class LoggerWrapper : Logger {

    protected abstract val delegate: Logger

    override fun level(level: LogLevel): Logger = delegate.level(level)

    override fun tag(tag: String): Logger = delegate.tag(tag)

    override fun printer(printer: Printer): Logger = delegate.printer(printer)

    override fun invoke(fn: () -> Any?): Logger = delegate.invoke(fn)
}