package io.grule.util.log

import java.text.SimpleDateFormat
import java.util.*

actual object SysPrinter : Printer {
    private const val DATE_FORMAT = "HH:mm:ss:SSS";

    override fun print(message: String) {
        val date = SimpleDateFormat(DATE_FORMAT).format(Date())
        println("$date  $message")
    }
}