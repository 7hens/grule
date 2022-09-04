package io.grule.matcher2.lexer

import io.grule.matcher.MatcherContext

interface LexerMatcherContext {
    val position: Int

    fun peek(offset: Int = 0): Char?

    fun getText(count: Int): String

    fun next(count: Int = 1): LexerMatcherContext {
        return Next(this, count)
    }

    companion object {
        fun from(context: MatcherContext, position: Int = 0): LexerMatcherContext {
            return Impl(context, position)
        }
    }

    private class Next(val prev: LexerMatcherContext, val count: Int) : LexerMatcherContext {
        override val position: Int get() = prev.position + count

        override fun peek(offset: Int): Char? {
            return prev.peek(position + offset)
        }

        override fun getText(count: Int): String {
            return prev.getText(position + count)
        }
    }

    private class Impl(val context: MatcherContext, override val position: Int) : LexerMatcherContext {
        override fun peek(offset: Int): Char? {
            return context.peek(position + offset)
        }

        override fun getText(count: Int): String {
            return context.getText(position, position + count)
        }
    }
}
