package io.grule.lexer

internal class LexerAction(val lexer: Lexer, val fn: (TokenStream, Int) -> Unit) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        return lexer.match(charStream, offset)
    }
    
    override fun parse(tokenStream: TokenStream, offset: Int): Int {
        val matchNum = match(tokenStream.charStream, offset)
        fn(tokenStream, matchNum)
        return matchNum
    }
}