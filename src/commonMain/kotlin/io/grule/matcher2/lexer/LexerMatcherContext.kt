package io.grule.matcher2.lexer

interface LexerMatcherContext {
    val position: Int

    fun peek(offset: Int = 0): Char?

    fun getText(count: Int): String

    fun next(count: Int = 1): LexerMatcherContext {
        val prev = this
        return object : LexerMatcherContext by prev {
            override val position: Int
                get() = prev.position + count
        }
    }
}
