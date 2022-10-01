package io.grule.token

import kotlin.math.abs

interface TextRange {

    val start: TextPosition

    val end: TextPosition

    val offset: Int get() = end.index - start.index

    val length: Int get() = abs(offset)

    fun isEmpty(): Boolean {
        return offset == 0
    }

    companion object {
        
        fun of(start: TextPosition, end: TextPosition = start): TextRange {
            return TextRangeImpl(start, end)
        }
    }

    private data class TextRangeImpl(
        override val start: TextPosition,
        override val end: TextPosition,
    ) : TextRange
}