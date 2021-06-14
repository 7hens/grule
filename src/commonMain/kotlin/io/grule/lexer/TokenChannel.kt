package io.grule.lexer

class TokenChannel(val input: CharStream, scanner: Scanner) : Scanner by scanner {
    private val buffer = mutableListOf<Token>()
    private var eof: Token? = null

    fun peek(offset: Int): Token {
        require(offset >= 0)
        prepare(offset + 1)
        if (offset < buffer.size) {
            return buffer[offset]
        }
        return requireNotNull(eof)
    }

    fun moveNext(count: Int) {
        if (count == 0) {
            return
        }
        require(count > 0)
        require(count <= buffer.size)
        repeat(count) {
            buffer.removeFirst()
        }
    }

    private fun prepare(expectedNum: Int) {
        var bufferSize = buffer.size
        while (eof == null && expectedNum > bufferSize) {
            scan(this)
            bufferSize = buffer.size
        }
    }

    fun emit(token: Token) {
        require(eof == null)
        buffer.add(token)
        if (token.lexer == Lexer.EOF) {
            eof = token
        }
    }

    fun emit(lexer: Lexer, text: String) {
        emit(Token(lexer, text, input))
    }

    fun emitEOF() {
        emit(Lexer.EOF, "<EOF>")
    }

    override fun toString(): String {
        val builder = StringBuilder()
        var i = 0
        while (true) {
            val token = peek(i)
            builder.append(token).append("\n")
            if (token.lexer == Lexer.EOF) {
                break
            }
            i++
        }
        return builder.toString()
    }
}