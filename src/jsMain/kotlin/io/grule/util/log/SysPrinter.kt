package io.grule.util.log

actual object SysPrinter : Printer {
    override fun print(level: LogLevel, tag: String, message: Any?) {
        val date = js("new Date().toString()")
        println("[${level.name.first()}. $date #$tag] $message")
    }
}