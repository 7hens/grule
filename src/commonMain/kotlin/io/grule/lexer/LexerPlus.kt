package io.grule.lexer

internal class LexerPlus(private val lexers: List<Lexer>) : Lexer {
    init {
        require(lexers.isNotEmpty())
    }

    override fun match(context: LexerContext, offset: Int): Int {
        var result = 0
        for (lexer in lexers) {
            result += lexer.match(context, offset + result)
        }
        return result
    }

    override fun plus(lexer: Lexer): Lexer {
        return LexerPlus(lexers + lexer)
    }

    override fun toString(): String {
        return lexers.joinToString("  ")
    }
}