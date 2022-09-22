package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.token.CharStream

class LexerStatus(
    override val key: Any,
    val data: CharStream,
    val position: Int = 0,
) : Status<LexerStatus> {

    override fun withKey(key: Any): LexerStatus {
        return LexerStatus(key, data, position)
    }

    fun next(count: Int): LexerStatus {
        if (count == 0) {
            return this
        }
        if (peek() == null) {
            panic(Lexer.EOF)
        }
        return LexerStatus(key, data, position + count)
    }

    fun peek(offset: Int = 0): Char? {
        return data.peek(position + offset)
    }

    fun getText(count: Int): String {
        return data.getText(position, position + count)
    }

    override fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override fun next(): LexerStatus {
        return next(1)
    }

    override fun self(): LexerStatus {
        return this
    }

    override fun self(matcher: Matcher<LexerStatus>): LexerStatus {
        return matcher.match(this)
    }
}
