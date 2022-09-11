package io.grule.lexer

import io.grule.matcher2.Matcher
import io.grule.token.MatcherContext

class LexerMatcherStatus private constructor(
    override val context: Matcher.Context,
    val position: Int = 0,
) : Matcher.Status<LexerMatcherStatus> {

    private val matcherContext: Matcher.Prop<MatcherContext> get() = prop("matcherContext")

    val data: MatcherContext get() = matcherContext.get()!!

    fun peek(offset: Int = 0): Char? {
        return data.peek(position + offset)
    }

    fun getText(count: Int): String {
        return data.getText(position, position + count)
    }

    fun next(count: Int): LexerMatcherStatus {
        if (peek() == null) {
            throw LexerMatcherException(this, "EOF")
        }
        return LexerMatcherStatus(context, position + count)
    }

    fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override fun next(): LexerMatcherStatus {
        return next(1)
    }

    override fun self(): LexerMatcherStatus {
        return this
    }

    override fun apply(matcher: Matcher<LexerMatcherStatus>): LexerMatcherStatus {
        val result = matcher.match(this)
        lastMatcher.set(matcher)
        return result
    }

    companion object {
        fun from(matcherContext: MatcherContext): LexerMatcherStatus {
            val instance = LexerMatcherStatus(Matcher.context())
            instance.matcherContext.set(matcherContext)
            return instance
        }
    }
}
