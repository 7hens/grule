package io.grule.matcher2.lexer

import io.grule.matcher.MatcherContext
import io.grule.matcher2.Matcher

class LexerMatcherStatus(
    val data: MatcherContext,
    val position: Int = 0,
) : Matcher.Status<LexerMatcherStatus> {

    fun peek(offset: Int = 0): Char? {
        return data.peek(position + offset)
    }

    fun getText(count: Int): String {
        return data.getText(position, position + count)
    }

    fun next(count: Int): LexerMatcherStatus {
        return LexerMatcherStatus(data, position + count)
    }

    fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override var lastMatcher: Matcher<LexerMatcherStatus>? = null
        private set

    override fun next(): LexerMatcherStatus {
        return next(1)
    }

    override fun apply(matcher: Matcher<LexerMatcherStatus>): LexerMatcherStatus {
        val result = matcher.match(this)
        lastMatcher = matcher
        return result
    }
}
