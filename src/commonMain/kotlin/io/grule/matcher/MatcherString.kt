package io.grule.matcher

internal class MatcherString(private val text: String) : Matcher {

    override fun match(context: MatcherContext, offset: Int): Int {
        context.peek(offset + text.length)
        if (context.peek(offset) == null) {
            throw MatcherException(context, text, "<EOF>")
        }
        val actualText = context.getText(offset, offset + text.length)
        if (actualText == text) {
            return text.length
        }
        throw MatcherException(context, text, actualText)
    }

    override fun toString(): String {
        return text
    }
}
