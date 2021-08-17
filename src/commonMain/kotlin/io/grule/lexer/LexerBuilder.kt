package io.grule.lexer

internal open class LexerBuilder : Lexer() {
    private var myLexer: Lexer = LexerPlus(mutableListOf())

    override fun match(input: CharStream, offset: Int): Int {
        myLexer.name = name
        return myLexer.match(input, offset)
    }

    override fun plus(lexer: Lexer): Lexer {
        myLexer = myLexer.plus(lexer)
        return this
    }

    override fun or(lexer: Lexer): Lexer {
        myLexer = myLexer.or(lexer)
        return this
    }
}