package io.grule.lexer

interface CharStream {
    val position: TextPosition
    
    fun peek(offset: Int): Int

    fun moveNext(count: Int)

    fun getText(startOffset: Int, endOffset: Int): String

    companion object {
        const val EOF = -1

        fun fromString(text: String): CharStream {
            return CharReader.fromString(text).toStream()
        }
    }
}
