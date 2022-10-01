package io.grule.token

internal class CharStreamImpl(private val reader: CharReader, private val chunkSize: Int) : CharStream {
    private var buffer = CharArray(chunkSize)
    private var dataStartPos = 0
    private var dataEndPos = 0
    private var currentIndex = 0

    private var currentLine = 1
    private var currentColumn = 1

    override val position: TextPosition
        get() = TextPosition(currentIndex, currentLine, currentColumn)

    override fun peek(offset: Int): Char? {
        require(offset >= 0) { "Offset ($offset) should be Greater than 0" }
        prepare(offset + 1)
        val dataOffset = dataStartPos + offset
        if (dataOffset < dataEndPos) {
            return buffer[dataOffset]
        }
        require(isReadOver) { "Not read over, offset $offset, position $position" }
        return null
    }

    override fun moveNext(count: Int): TextRange {
        require(count >= 0) { "Cannot move back, count is $count" }
        if (count == 0) {
            return TextRange.of(position)
        }
        val startPosition = position
        val newDataStartOffset = dataStartPos + count
        require(newDataStartOffset <= dataEndPos) { "Move to $newDataStartOffset out of bounds ($dataEndPos)" }
        for (i in dataStartPos until newDataStartOffset) {
            if (buffer[i] == '\n') {
                currentLine++
                currentColumn = 1
            } else {
                currentColumn++
            }
        }
        dataStartPos = newDataStartOffset
        currentIndex += count
        return TextRange.of(startPosition, position)
    }

    override fun getText(start: Int, end: Int): String {
        require(start in 0..end) { "Start pos $start not in range $end" }
        val offset = dataStartPos + start
        val length = minOf(end - start, dataEndPos - offset)
        return buffer.concatToString(offset, offset + length)
    }

    private var isReadOver = false

    private fun prepare(expectedNum: Int) {
        val preparedNum = dataEndPos - dataStartPos
        if (isReadOver || preparedNum >= expectedNum) {
            return
        }
        val expectedReadTimes = (expectedNum - preparedNum - 1) / chunkSize + 1
        val expectedReadNum = expectedReadTimes * chunkSize
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
            val readNum = reader.read(buffer, dataEndPos, chunkSize)
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
        if (newBufferSize > buffer.size || restNum >= chunkSize) {
            newBuffer = CharArray(newBufferSize)
        }
        buffer.copyInto(newBuffer, 0, dataStartPos, dataEndPos)
        buffer = newBuffer
        dataStartPos = 0
        dataEndPos = preparedNum
    }
}
