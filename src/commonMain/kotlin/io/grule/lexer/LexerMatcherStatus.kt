package io.grule.lexer

import io.grule.matcher.Context
import io.grule.matcher.Matcher
import io.grule.matcher.Prop
import io.grule.matcher.Status
import io.grule.token.CharStream

class LexerMatcherStatus private constructor(
    override val context: Context,
    val position: Int = 0,
    override val key: Any,
) : Status<LexerMatcherStatus> {

    private val charStreamProp: Prop<CharStream> get() = prop("charStream")

    val data: CharStream get() = charStreamProp.get()!!

    fun peek(offset: Int = 0): Char? {
        return data.peek(position + offset)
    }

    fun getText(count: Int): String {
        return data.getText(position, position + count)
    }

    fun next(count: Int): LexerMatcherStatus {
        if (count == 0) {
            return this
        }
        if (peek() == null) {
            panic(Lexer.EOF)
        }
        return LexerMatcherStatus(context, position + count, key)
    }

    override fun key(key: Any): LexerMatcherStatus {
        return LexerMatcherStatus(context, position, key)
    }

    override fun panic(rule: Any): Nothing {
        throw LexerMatcherException(this, rule)
    }

    override fun next(): LexerMatcherStatus {
        return next(1)
    }

    override fun self(): LexerMatcherStatus {
        return this
    }

    companion object {
        fun from(charStream: CharStream, position: Int = 0): LexerMatcherStatus {
            val instance = LexerMatcherStatus(Matcher.context(), position, Unit)
            instance.charStreamProp.set(charStream)
            return instance
        }
    }
}
