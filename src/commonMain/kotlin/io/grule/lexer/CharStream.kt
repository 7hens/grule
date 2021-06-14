package io.grule.lexer

class CharStream(private val reader: CharReader,
                 private val readChunkSize: Int = DEFAULT_CHUNK_SIZE) {
    private var buffer = CharArray(readChunkSize)
    private var dataStartPos = 0
    private var dataEndPos = 0
    private var currentIndex = 0

    private var currentLine = 1
    private var currentColumn = 1

    fun index() = currentIndex

    fun line() = currentLine

    fun column() = currentColumn

    fun peek(offset: Int): Int {
        require(offset >= 0)
        prepare(offset + 1)
        val dataOffset = dataStartPos + offset
        if (dataOffset < dataEndPos) {
            return buffer[dataOffset].toInt()
        }
        require(isReadOver)
        return EOF
    }

    fun moveNext(count: Int) {
        if (count == 0) {
            return
        }
        require(count > 0)
        val newDataStartOffset = dataStartPos + count
        require(newDataStartOffset <= dataEndPos)
        for(i in dataStartPos until newDataStartOffset) {
            if (buffer[i] == '\n') {
                currentLine++
                currentColumn = 1
            } else {
                currentColumn++
            }
        }
        dataStartPos = newDataStartOffset
        currentIndex += count
    }

    fun getText(startOffset: Int, endOffset: Int): String {
        require(startOffset >= 0)
        require(endOffset >= startOffset)
        require(endOffset <= dataEndPos - dataStartPos)
        val offset = dataStartPos + startOffset
        return buffer.concatToString(offset, offset + (endOffset - startOffset))
    }

    private var isReadOver = false

    private fun prepare(expectedNum: Int) {
        val preparedNum = dataEndPos - dataStartPos
        if (isReadOver || preparedNum >= expectedNum) {
            return
        }
        val expectedReadTimes = (expectedNum - preparedNum - 1) / readChunkSize + 1
        val expectedReadNum = expectedReadTimes * readChunkSize
        val expectedBufferSize = preparedNum + expectedReadNum
        if (expectedBufferSize > buffer.size) {
            var newBufferSize = buffer.size
            while (newBufferSize < expectedBufferSize) {
                newBufferSize *= 2
            }
            adjustBufferSize(newBufferSize)
        } else {
            val expectedDataEndOffset = dataEndPos + expectedReadNum
            if (expectedDataEndOffset >= buffer.size) {
                adjustBufferSize(buffer.size)
            }
        }
        for (i in 1..expectedReadNum) {
            val readNum = reader.read(buffer, dataEndPos, readChunkSize)
            if (readNum < 0) {
                isReadOver = true
                break
            }
            dataEndPos += readNum
        }
    }

    private fun adjustBufferSize(newBufferSize: Int) {
        val preparedNum = dataEndPos - dataStartPos
        val restNum = newBufferSize - preparedNum
        var newBuffer = buffer
        if (newBufferSize > buffer.size || restNum >= readChunkSize) {
            newBuffer = CharArray(newBufferSize)
        }
        buffer.copyInto(newBuffer, 0, dataStartPos, dataEndPos)
        buffer = newBuffer
        dataStartPos = 0
        dataEndPos = preparedNum
    }

    companion object {
        const val EOF = -1
        const val DEFAULT_CHUNK_SIZE = 8 * 1024
    }
}