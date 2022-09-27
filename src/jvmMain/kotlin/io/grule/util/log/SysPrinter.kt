package io.grule.util.log

import java.text.SimpleDateFormat
import java.util.*

actual object SysPrinter : Printer {
    private const val DATE_FORMAT = "HH:mm:ss:SSS";

    override fun print(level: LogLevel, tag: String, message: Any?) {
        val date = SimpleDateFormat(DATE_FORMAT).format(Date())
        println("[${level.name.first()}. $date #$tag] $message")
    }
}