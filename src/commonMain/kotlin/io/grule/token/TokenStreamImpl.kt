package io.grule.token

import io.grule.lexer.Lexer
import io.grule.lexer.LexerContextImpl

internal class TokenStreamImpl(charStream: CharStream, val lexer: Lexer) : TokenStream {
    private val buffer = mutableListOf<Token>()
    private var eof: Token? = null
    private val context = LexerContextImpl(charStream, this)

    override fun peek(offset: Int): Token {
        require(offset >= 0) { "Offset $offset should not be negative" }
        prepare(offset + 1)
        if (offset < buffer.size) {
            return buffer[offset]
        }
        return requireNotNull(eof)
    }

    override fun all(): Sequence<Token> {
        return sequence {
            var i = 0
            while (true) {
                val token = peek(i)
                yield(token)
                if (token.lexer == Lexer.EOF) {
                    break
                }
                i++
            }
        }
    }

    override fun moveNext(count: Int) {
        require(count in buffer.indices) { "Count $count not in range ${buffer.size}" }
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
        require(eof == null) { "Already end of file" }
        buffer.add(token)
        if (token.lexer == Lexer.EOF) {
            eof = token
        }
    }
}