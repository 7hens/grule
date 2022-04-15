package io.grule.lexer

import io.grule.Grule
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class RegexGrammar : Grule() {
    val EscapeChar by token(L + "\\" - "abtrvfneSsDdWw")
    val EscapeOperator by token(L + "\\" - REG_OPERATORS)
    val Unicode by token(L + "\\u" + L_hex.repeat(4, 4))
    val Hex by token(L + "\\x" + L_hex.repeat(2, 2))
    val Octal by token(L + "\\" + L_octal.repeat(3, 3) or L + "\\0")
    val Digit by token(L + L_digit)

    init {
        token(L - REG_OPERATORS)
    }
    
    val Char by token(L_any)

    val regex: Parser by p { P + (P + branch).join(P + "|") }
    val escape by P + EscapeOperator or P + EscapeChar or P + Unicode or P + Hex or P + Octal
    val char by P + escape or P + Digit or P + Char or P + "."
    val item by P + char + "-" + char or P + char
    val atom by P + char or P + "(" + regex + ")" or P + "[" + (P + "^").optional() + item.repeat(1) + "]"
    val digits by P + (P + Digit).repeat(1)
    val quantity by P + digits + "," + (P + digits).optional() or P + digits
    val quantifier by P + "+" or P + "*" or P + "?" or P + "{" + quantity + "}"
    val piece by P + atom + (P + "+" or P + "*") + "?" + atom or P + atom + quantifier.optional()
    val branch by P + piece.repeat()


    companion object {
        const val REG_OPERATORS = "\\()[]{}|.-?+*^\$"
    }
}
