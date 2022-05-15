package io.grule.lexer

import io.grule.matcher.CharStream
import io.grule.matcher.Matcher

internal class TokenStreamImpl(val charStream: CharStream, val lexer: Lexer) : TokenStream {
    private val buffer = mutableListOf<Token>()
    private var eof: Token? = null
    private val context = LexerContextImpl(charStream, this)

    override fun peek(offset: Int): Token {
        require(offset >= 0)
        prepare(offset + 1)
        if (offset < buffer.size) {
            return buffer[offset]
        }
        return requireNotNull(eof)
    }

    override fun moveNext(count: Int) {
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
        while (eof == null && bufferSize < expectedNum) {
            lexer.lex(context)
            bufferSize = buffer.size
        }
    }

    override fun emit(token: Token) {
        require(eof == null)
        buffer.add(token)
        if (token.lexer == Matcher.EOF) {
            eof = token
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        var i = 0
        while (true) {
            val token = peek(i)
            builder.append(token).append("\n")
            if (token.lexer == Lexers.EOF) {
                break
            }
            i++
        }
        return builder.toString()
    }
}