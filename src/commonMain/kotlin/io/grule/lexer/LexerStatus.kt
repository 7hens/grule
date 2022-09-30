package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.token.CharStream

class LexerStatus(
    val data: CharStream,
    val position: Int = data.charIndex,
) : Status<LexerStatus> {

    override fun equals(other: Any?): Boolean {
        if (other is LexerStatus) {
            return other.data == data && other.position == position
        }
        return false
    }

    override fun hashCode(): Int {
        return listOf(data, position).hashCode()
    }

    private val positionOffset get() = position - data.charIndex

    override fun move(step: Int): LexerStatus {
        if (step == 0) {
            return this
        }
        if (peek() == null) {
            panic(Lexer.EOF)
        }
        return LexerStatus(data, maxOf(position + step, data.charIndex))
    }

    fun peek(offset: Int = 0): Char? {
        return data.peek(positionOffset + offset)
    }

    fun getText(count: Int): String {
        return data.getText(positionOffset, positionOffset + count)
    }

    override fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override fun self(matcher: Matcher<LexerStatus>): LexerStatus {
        return matcher.match(this)
    }

    override fun selfPartial(matcher: Matcher<LexerStatus>): LexerStatus {
        return matcher.match(this)
    }

    override fun toString(): String {
        return "#$position, ${data.position}, ${data.peek(0)}"
    }
}
