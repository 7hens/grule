package io.grule.matcher2.parser

import io.grule.lexer.Token

interface ParserMatcherContext {
    fun peek(offset: Int): Token
}