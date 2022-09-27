package io.grule.util.log

interface Logger {

    fun level(level: LogLevel): Logger

    fun tag(tag: String): Logger

    fun printer(printer: Printer): Logger

    operator fun invoke(fn: () -> Any?): Logger

    operator fun invoke(message: Any?): Logger {
        return invoke { message }
    }

    val verbose: Logger get() = level(LogLevel.VERBOSE)

    val debug: Logger get() = level(LogLevel.DEBUG)

    val info: Logger get() = level(LogLevel.INFO)

    val warn: Logger get() = level(LogLevel.WARN)

    val error: Logger get() = level(LogLevel.ERROR)

    fun on(condition: Boolean): Logger {
        if (condition) {
            return this
        }
        return LoggerEmpty
    }

    companion object : LoggerWrapper() {

        override var delegate: Logger = builder().build()

        fun setDefault(builder: LoggerBuilder) {
            delegate = builder.build()
        }

        fun builder(): LoggerBuilder {
            return LoggerBuilderImpl(LogLevel.INFO, "", SysPrinter)
        }
    }

}