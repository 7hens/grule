package io.grule.matcher2.parser

import io.grule.lexer.Token

interface ParserMatcherContext {
    val position: Int

    fun peek(offset: Int = 0): Token

    fun next(count: Int = 1): ParserMatcherContext {
        val prev = this
        return object : ParserMatcherContext by prev {
            override val position: Int
                get() = prev.position + count
        }
    }
}