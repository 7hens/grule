package io.grule.lexer

import io.grule.Grule
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class RegexGrammar : Grule() {
    val EscapeChar by token(L + "\\" - "abtrvfne" or L + "\\" - REG_OPERATORS)
    val Unicode by token(L + "\\u" + L_hex.repeat(4, 4))
    val Hex by token(L + "\\x" + L_hex.repeat(2, 2))
    val Octal by token(L + "\\" + L_octal.repeat(3, 3) or L + "\\0")
    val Digit by token(L + L_digit)
    val CharClass by token(L + "\\" - "SsDdWw" or L + ".")

    init {
        token(L - REG_OPERATORS)
    }

    val Char by token(L_any)

    val branch: Parser by p { P + piece.repeat(1) }
    val regex by P + (P + branch).join(P + "|")
    val specChar by P + EscapeChar or P + Unicode or P + Hex or P + Octal or P + Digit or P + Char
    val char by P + CharClass or P + specChar
    val item by P + specChar + "-" + specChar or P + char
    val atom by P + char or
            P + "(" + "?" + "=" + regex + ")" or
            P + "(" + "?" + "!" + regex + ")" or
            P + "(" + regex + ")" or
            P + "[" + (P + "^").optional() + item.repeat(1) + "]"
    val digits by P + (P + Digit).repeat(1)
    val quantity by P + digits + "," + (P + digits).optional() or P + digits
    val quantifier by (P + "+" or P + "*" or P + "{" + quantity + "}") + (P + "?").optional()
    val piece by P + atom + quantifier + branch.optional() or P + atom + (P + "?").optional()

    companion object {
        const val REG_OPERATORS = "\\()[]{}|.-?+*^\$"
    }
}
