package io.grule.lexer

internal class LexerString(private val text: String) : Lexer {

    override fun match(context: LexerContext, offset: Int): Int {
        context.peek(offset + text.length)
        if (context.peek(offset) == null) {
            throw LexerException(context, text, "<EOF>")
        }
        val actualText = context.getText(offset, offset + text.length)
        if (actualText == text) {
            return text.length
        }
        throw LexerException(context, text, actualText)
    }

    override fun toString(): String {
        return text
    }
}
