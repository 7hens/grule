package io.grule.lexer

import io.grule.matcher.CharStream
import io.grule.matcher.Matcher

internal class TokenStreamImpl(charStream: CharStream, val lexer: Lexer) : TokenStream {
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

    override fun all(): List<Token> {
        val list = mutableListOf<Token>()
        var i = 0
        while (true) {
            val token = peek(i)
            list.add(token)
            if (token.lexer == Lexer.EOF) {
                break
            }
            i++
        }
        return list
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
}