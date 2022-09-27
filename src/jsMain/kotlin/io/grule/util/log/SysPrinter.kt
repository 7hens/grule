package io.grule.util.log

actual object SysPrinter : Printer {
    private const val MAX_LENGTH = 1000
    private const val END_LENGTH = 20

    override fun print(level: LogLevel, tag: String, message: Any?) {
        val date = js("new Date().toString()")
        println("[${level.name.first()}. $date #$tag] ${shortText(message.toString())}")
    }

    private fun shortText(text: String): String {
        if (text.length <= MAX_LENGTH) {
            return text
        }
        return text.substring(0, MAX_LENGTH - END_LENGTH - 3) + "..." + text.substring(text.length - END_LENGTH)
    }
}