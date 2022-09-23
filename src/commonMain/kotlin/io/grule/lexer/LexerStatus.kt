package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.token.CharStream

class LexerStatus(
    val data: CharStream,
    val position: Int = 0,
) : Status<LexerStatus> {

    fun next(count: Int): LexerStatus {
        if (count == 0) {
            return this
        }
        if (peek() == null) {
            panic(Lexer.EOF)
        }
        return LexerStatus(data, position + count)
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

    override fun self(matcher: Matcher<LexerStatus>): LexerStatus {
        return matcher.match(this)
    }

    override fun selfPartial(matcher: Matcher<LexerStatus>): LexerStatus {
        return matcher.match(this)
    }
}
