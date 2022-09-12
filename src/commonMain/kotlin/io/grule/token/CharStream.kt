package io.grule.token

interface CharStream {

    val position: TextPosition

    fun peek(offset: Int): Char?

    fun getText(start: Int, end: Int): String

    fun moveNext(count: Int)

    companion object {
        fun fromString(text: String): CharStream {
            return CharReader.fromString(text).toStream()
        }
    }
}
