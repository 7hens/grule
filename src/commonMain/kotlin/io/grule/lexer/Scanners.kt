package io.grule.lexer

object Scanners {
    val EOF = object : Scanner() {
        override fun scan(charStream: CharStream, tokenStream: TokenStream) {
            Lexer.EOF.match(charStream)
            tokenStream.emit(this, "<EOF>")
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
        }
    }

    fun token(lexer: Lexer): Scanner {
        return object : Scanner() {
            override fun scan(charStream: CharStream, tokenStream: TokenStream) {
                val matchNum = lexer.match(charStream)
                tokenStream.emit(this, charStream.getText(0, matchNum))
                charStream.moveNext(matchNum)
            }
        }
    }
}