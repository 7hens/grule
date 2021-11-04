package io.grule.lexer

object Scanners {
    val EOF = object : Scanner() {
        override fun scan(tokenStream: TokenStream) {
            Lexer.EOF.match(tokenStream.charStream)
            tokenStream.emit(this, "<EOF>")
        }
    }

    val EMPTY = object : Scanner() {
        override fun scan(tokenStream: TokenStream) {
        }
    }

    fun skip(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(tokenStream: TokenStream) {
                val charStream = tokenStream.charStream
                val matchNum = lexer.match(charStream)
                charStream.moveNext(matchNum)
            }
        }
    }

    fun token(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(tokenStream: TokenStream) {
                val charStream = tokenStream.charStream
                val matchNum = lexer.match(charStream)
                tokenStream.emit(this, charStream.getText(0, matchNum))
                charStream.moveNext(matchNum)
            }
        }
    }

    fun create(lexer: Lexer, fn: TokenStream.(Int) -> Unit): Scanner {
        return object : Scanner() {
            override fun scan(tokenStream: TokenStream) {
                val charStream = tokenStream.charStream
                val matchNum = lexer.match(charStream)
                fn(tokenStream, matchNum)
            }
        }
    }
}