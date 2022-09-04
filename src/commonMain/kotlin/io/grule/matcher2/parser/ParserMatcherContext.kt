package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream

interface ParserMatcherContext {
    val position: Int

    fun peek(offset: Int = 0): Token

    fun next(count: Int = 1): ParserMatcherContext {
        return Next(this, count)
    }

    companion object {
        fun from(tokenStream: TokenStream, position: Int = 0): ParserMatcherContext {
            return Impl(tokenStream, position)
        }
    }

    private class Next(val prev: ParserMatcherContext, val count: Int) : ParserMatcherContext {
        override val position: Int get() = prev.position + count

        override fun peek(offset: Int): Token {
            return prev.peek(position + offset)
        }
    }

    private class Impl(val tokenStream: TokenStream, override val position: Int) : ParserMatcherContext {
        override fun peek(offset: Int): Token {
            return tokenStream.peek(position + offset)
        }
    }
}