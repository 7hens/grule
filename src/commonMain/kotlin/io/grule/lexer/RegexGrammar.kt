package io.grule.lexer

import io.grule.Grule
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class RegexGrammar : Grule() {
    val EscapeChar by token(L + "\\" - "abtrvfneSsDdWw")
    val EscapeOperator by token(L + "\\" - REG_OPERATORS)
    val Unicode by token(L + "\\u" + (L_digit or L - "ABCDEFabcdef").repeat(4, 4))
    val Hex by token(L + "\\x" + (L_digit or L - "ABCDEFabcdef").repeat(2, 2))
    val Octal by token(L + "\\" + (L - ('0'..'7')).repeat(3, 3))
    val Digit by token(L + L_digit)
    val Char by token(L + L_letter or L - "`~@#$%&_=:;'\",<>/ ")

    init {
        token(L - REG_OPERATORS)
    }

    val regex: Parser by p { P + (P + branch).join(P + "|") }
    val escape by P + EscapeOperator or P + EscapeChar or P + Unicode or P + Hex or P + Octal
    val char by P + escape or P + Digit or P + Char
    val item by P + char + "-" + char or P + char
    val atom by P + char or P + "(" + regex + ")" or P + "[" + (P + "^").optional() + item.repeat(1) + "]"
    val digits by P + (P + Digit).repeat(1)
    val quantity by P + digits + "," + (P + digits).optional() or P + digits
    val quantifier by P + (P + "+" or P + "*") + (P + "?").optional() or P + "?" or P + "{" + quantity + "}"
    val piece by P + atom + quantifier.optional()
    val branch by P + piece.repeat()


    companion object {
        const val REG_OPERATORS = "\\()[]{}|.-?+*^\$"
    }
}
