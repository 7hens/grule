package io.grule.lexer

import io.grule.matcher.Matcher

object Lexers {
    val EOF = object : Lexer() {
        override fun lex(context: LexerContext) {
            Matcher.EOF.match(context)
            context.emit(this)
        }

        override fun toString(): String {
            return Matcher.EOF.toString()
        }
    }

    val EMPTY = object : Lexer() {
        override fun lex(context: LexerContext) {
        }
    }

    fun skip(matcher: Matcher): Lexer {
        return object : Lexer() {
            override fun lex(context: LexerContext) {
                val matchNum = matcher.match(context)
                context.moveNext(matchNum)
            }
        }.apply { name = matcher.toString() }
    }

    fun token(matcher: Matcher): Lexer {
        return object : Lexer() {
            override fun lex(context: LexerContext) {
                val matchNum = matcher.match(context)
                context.emit(this, matchNum)
                context.moveNext(matchNum)
            }
        }.apply { name = matcher.toString() }
    }
}