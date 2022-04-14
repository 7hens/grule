package io.grule.lexer

internal class LexerOr(
    private val lexers: MutableList<Lexer>) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        for (lexer in lexers) {
            try {
                return lexer.match(charStream, offset)
            } catch (_: LexerException){
                continue
            }
        }
        throw LexerException(toString())
    }

    override fun or(lexer: Lexer): Lexer {
        lexers.add(lexer)
        return this
    }

    override fun toString(): String {
        return lexers.joinToString(" | ")
    }
}