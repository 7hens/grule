package io.grule.scanner

import io.grule.lexer.Lexer

object Scanners {
    val EOF = object : Scanner() {
        override fun scan(context: ScannerContext) {
            Lexer.EOF.match(context)
            context.emit(this)
        }

        override fun toString(): String {
            return Lexer.EOF.toString()
        }
    }

    val EMPTY = object : Scanner() {
        override fun scan(context: ScannerContext) {
        }
    }

    fun skip(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(context: ScannerContext) {
                val matchNum = lexer.match(context)
                context.moveNext(matchNum)
            }
        }.apply { name = lexer.toString() }
    }

    fun token(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(context: ScannerContext) {
                val matchNum = lexer.match(context)
                context.emit(this, matchNum)
                context.moveNext(matchNum)
            }
        }.apply { name = lexer.toString() }
    }
}