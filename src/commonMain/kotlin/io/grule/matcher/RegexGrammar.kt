package io.grule.matcher

import io.grule.Grammar
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class RegexGrammar : Grammar() {
    val EscapeChar by lexer { X + "\\" - "abtrvfne" or X + "\\" - REG_OPERATORS }
    val Unicode by lexer { X + "\\u" + HEX.repeat(4, 4) }
    val Hex by lexer { X + "\\x" + HEX.repeat(2, 2) }
    val Octal by lexer { X + "\\" + OCTAL.repeat(3, 3) or X + "\\0" }
    val Digit by lexer { DIGIT }
    val CharClass by lexer { X + "\\" - "SsDdWw" or X + "." }

    init {
        lexer.token { X - REG_OPERATORS }
    }

    val Char by lexer { ANY }

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
