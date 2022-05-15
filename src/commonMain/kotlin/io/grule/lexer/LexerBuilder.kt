package io.grule.lexer

internal open class LexerBuilder : Lexer {
    private var myLexer: Lexer = LexerShadow

    override fun match(context: LexerContext, offset: Int): Int {
        return myLexer.match(context, offset)
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

}