package io.grule.util.log

actual object SysPrinter : Printer {
    override fun print(message: String) {
        val date = js("new Date().toString()")
        println("$date $message")
    }
}