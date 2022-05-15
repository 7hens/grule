package io.grule.lexer

object Scanners {
    val EOF = object : Scanner() {
        override fun scan(charStream: CharStream, tokenStream: TokenStream) {
            Lexer.EOF.match(charStream)
            tokenStream.emit(this)
        }

        override fun toString(): String {
            return Lexer.EOF.toString()
        }
    }

    val EMPTY = object : Scanner() {
        override fun scan(charStream: CharStream, tokenStream: TokenStream) {
        }
    }

    fun skip(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(charStream: CharStream, tokenStream: TokenStream) {
                val matchNum = lexer.match(charStream)
                charStream.moveNext(matchNum)
            }
        }.apply { name = lexer.toString() }
    }

    fun token(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(charStream: CharStream, tokenStream: TokenStream) {
                val matchNum = lexer.match(charStream)
                tokenStream.emit(this, charStream.getText(0, matchNum))
                charStream.moveNext(matchNum)
            }
        }.apply { name = lexer.toString() }
    }
}