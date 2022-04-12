package io.grule.lexer

internal open class LexerBuilder : Lexer() {
    private var myLexer: Lexer = Shadow

    override fun match(charStream: CharStream, offset: Int): Int {
        return myLexer.match(charStream, offset)
    }

    override fun plus(lexer: Lexer): Lexer {
        myLexer = myLexer.plus(lexer)
        return this
    }

    override fun or(lexer: Lexer): Lexer {
        myLexer = myLexer.or(lexer)
        return this
    }

    override fun toString(): String {
        return myLexer.toString()
    }

    object Shadow : Lexer() {
        override fun match(charStream: CharStream, offset: Int): Int {
            throw UnsupportedOperationException()
        }

        override fun plus(lexer: Lexer): Lexer {
            return LexerPlus(mutableListOf(lexer))
        }

        override fun or(lexer: Lexer): Lexer {
            return LexerOr(mutableListOf(lexer))
        }
    }
}