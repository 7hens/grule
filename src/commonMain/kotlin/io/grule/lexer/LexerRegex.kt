package io.grule.lexer

import io.grule.Grule
import io.grule.parser.Parser

internal class LexerRegex(private val regex: Regex) : Lexer() {
    private val lexer = parseRegex(regex)

    override fun match(charStream: CharStream, offset: Int): Int {
        return lexer.match(charStream, offset)
    }

    private fun parseRegex(regex: Regex): Lexer {
        return EOF
    }

    override fun toString(): String {
        return regex.pattern
    }

    companion object {
        const val REG_OPERATORS = "\\()[]{}|.-?+*^\$"
    }

    class RegexGrammar : Grule() {
        val Specific by token(L + "\\" - "abtrvfne")
        val Escape by token(L + "\\" - REG_OPERATORS)
        val Hex by token(L + "\\x" + L_digit.repeat(2, 2))
        val Unicode by token(L + "\\u" + L_digit.repeat(4, 4))
        val Digit by token(L + L_digit)
        val Letter by token(L + L_letter)

        init {
            token(L - REG_OPERATORS)
        }

        val regex: Parser by p { P + branch.join(P + "|") }
        val escape by P + Escape or P + Specific or P + Hex or P + Unicode
        val atom by  P + escape or P + Letter or P + Digit or P + "(" + regex + ")"
        val digits by P + (P + Digit).repeat(1)
        val quantity by P + "{" + digits + "," + (P + digits).optional() + "}"
        val quantifier by P + (P + "+" or P + "*") + (P + "?").optional() or P + "?" or P + quantity
        val piece by P + atom + quantifier.optional()
        val branch by P + piece.repeat()
    }
}
