package io.grule.util.log

internal class LoggerImpl(val level: LogLevel, val tag: String, val printer: Printer) : Logger {

    override fun level(level: LogLevel): Logger {
        return when {
            level == this.level -> this
            level > this.level -> LoggerImpl(level, tag, printer)
            else -> LoggerEmpty
        }
    }

    override fun tag(tag: String): Logger {
        return LoggerImpl(level, tag, printer)
    }

    override fun printer(printer: Printer): Logger {
        return LoggerImpl(level, tag, printer)
    }

    override fun invoke(fn: () -> String): Logger {
        printer.print("[${level.name.first()}__$tag] - ${fn()}")
        return this
    }
}