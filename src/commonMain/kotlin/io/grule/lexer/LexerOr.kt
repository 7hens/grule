package io.grule.lexer

internal class LexerOr(private val lexers: List<Lexer>) : Lexer {
    init {
        require(lexers.isNotEmpty())
    }
    
    override fun match(context: LexerContext, offset: Int): Int {
        for (lexer in lexers) {
            try {
                return lexer.match(context, offset)
            } catch (_: LexerException) {
                continue
            }
        }
        throw LexerException(toString())
    }

    override fun or(lexer: Lexer): Lexer {
        return LexerOr(lexers + lexer)
    }

    override fun toString(): String {
        return lexers.joinToString(" | ")
    }
}