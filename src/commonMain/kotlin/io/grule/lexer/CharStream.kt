package io.grule.lexer

interface CharStream {
    val position: TextPosition

    fun peek(offset: Int): Char?

    fun moveNext(count: Int)

    fun getText(startOffset: Int, endOffset: Int): String

    companion object {
        fun fromString(text: String): CharStream {
            return CharReader.fromString(text).toStream()
        }
    }
}
