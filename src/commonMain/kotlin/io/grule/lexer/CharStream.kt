package io.grule.lexer

interface CharStream {
    val charIndex: Int

    val line: Int

    val column: Int

    fun peek(offset: Int): Int

    fun moveNext(count: Int)

    fun getText(startOffset: Int, endOffset: Int): String

    companion object {
        const val EOF = -1
    }
}
