package io.grule.lexer

fun interface CharReader {
    fun read(buffer: CharArray, offset: Int, length: Int): Int

    companion object {
        fun fromString(text: String): CharReader {
            return StringReader(text)
        }

        fun fromByteArray(data: CharArray, startIndex: Int = 0, endIndex: Int = data.size): CharReader {
            return ByteArrayReader(data, startIndex, endIndex)
        }
    }

    private class StringReader(private val text: String) : CharReader {
        private var position = 0

        override fun read(buffer: CharArray, offset: Int, length: Int): Int {
            if (position >= text.length) {
                return -1
            }
            val readNum = minOf(length, buffer.size - offset, text.length - position)
            if (readNum <= 0) {
                return 0
            }
            if (readNum == 1) {
                buffer[offset] = text[position]
                position++
                return 1
            }
            text.toCharArray(position, position + readNum)
                .copyInto(buffer, offset, 0, readNum)
            position += readNum
            return readNum
        }
    }

    private class ByteArrayReader(
        private val data: CharArray,
        startIndex: Int,
        private val endIndex: Int) : CharReader {
        private var position = startIndex

        override fun read(buffer: CharArray, offset: Int, length: Int): Int {
            if (position >= endIndex) {
                return -1
            }
            val readNum = minOf(length, buffer.size - offset, endIndex - position)
            if (readNum <= 0) {
                return 0
            }
            if (readNum == 1) {
                buffer[offset] = data[position]
                position++
                return 1
            }
            data.copyInto(buffer, offset, position, position + readNum)
            position += readNum
            return readNum
        }
    }
}