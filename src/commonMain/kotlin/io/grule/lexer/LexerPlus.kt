package io.grule.lexer

internal class LexerPlus(private val lexers: MutableList<Lexer>) : Lexer() {

    override fun match(input: CharStream, offset: Int): Int {
        if (lexers.isEmpty()) {
            throw LexerException("Empty lexers")
        }
        var result = 0
        for (lexer in lexers) {
            result += lexer.match(input, offset + result)
        }
        return result
    }

    override fun plus(lexer: Lexer): Lexer {
        if (lexers.isEmpty()) return lexer
        lexers.add(lexer)
        return this
    }
}