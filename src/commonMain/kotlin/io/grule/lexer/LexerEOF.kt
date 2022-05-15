package io.grule.lexer

import io.grule.matcher.Matcher

object LexerEOF : Lexer() {
    override fun lex(context: LexerContext) {
        Matcher.EOF.match(context)
        context.emit(this)
    }

    override fun toString(): String {
        return Matcher.EOF.toString()
    }
}
