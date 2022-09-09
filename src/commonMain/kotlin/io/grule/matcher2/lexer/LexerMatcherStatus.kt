package io.grule.matcher2.lexer

import io.grule.matcher.MatcherContext
import io.grule.matcher2.Matcher

class LexerMatcherStatus(
    val data: MatcherContext,
    val position: Int = 0,
    override var lastMatcher: Matcher<LexerMatcherStatus>? = null
) : Matcher.Status<LexerMatcherStatus> {

    fun peek(offset: Int = 0): Char? {
        return data.peek(position + offset)
    }

    fun getText(count: Int): String {
        return data.getText(position, position + count)
    }

    fun next(count: Int): LexerMatcherStatus {
        return LexerMatcherStatus(data, position + count, lastMatcher)
    }

    fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override fun next(): LexerMatcherStatus {
        return next(1)
    }

    override fun self(isMe: Boolean): LexerMatcherStatus {
        return this
    }

    override fun apply(matcher: Matcher<LexerMatcherStatus>): LexerMatcherStatus {
        val result = matcher.match(this)
        result.lastMatcher = matcher
        return result
    }
}
