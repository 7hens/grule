package io.grule.util.log

internal class LoggerBuilderImpl(
    val level: LogLevel,
    val tag: String,
    val printer: Printer,
) : LoggerBuilder {

    override fun build(): Logger {
        return LoggerImpl(level, tag, printer)
    }

    override fun level(level: LogLevel): LoggerBuilder {
        return LoggerBuilderImpl(level, tag, printer)
    }

    override fun tag(tag: String): LoggerBuilder {
        return LoggerBuilderImpl(level, tag, printer)
    }

    override fun printer(printer: Printer): LoggerBuilder {
        return LoggerBuilderImpl(level, tag, printer)
    }
}