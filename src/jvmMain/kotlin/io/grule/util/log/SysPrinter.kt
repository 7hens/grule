package io.grule.util.log

import java.text.SimpleDateFormat
import java.util.*

actual object SysPrinter : Printer {
    private const val DATE_FORMAT = "HH:mm:ss:SSS"
    private const val MAX_LENGTH = 1000
    private const val END_LENGTH = 20

    override fun print(level: LogLevel, tag: String, message: Any?) {
        val date = SimpleDateFormat(DATE_FORMAT).format(Date())
        println("[${level.name.first()}. $date #$tag] ${shortText(message.toString())}")
    }

    private fun shortText(text: String): String {
        if (text.length <= MAX_LENGTH) {
            return text
        }
        return text.substring(0, MAX_LENGTH - END_LENGTH - 3) + "..." + text.substring(text.length - END_LENGTH)
    }
}