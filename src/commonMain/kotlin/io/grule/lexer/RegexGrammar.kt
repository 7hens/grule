package io.grule.lexer

import io.grule.Grammar
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class RegexGrammar : Grammar() {
    val EscapeChar by lexer { X + "\\" + X["atrvfne"] or X + "\\" + X[REG_OPERATORS] }
    val Unicode by lexer { X + "\\u" + HEX * 4 }
    val Hex by lexer { X + "\\x" + HEX * 2 }
    val Octal by lexer { X + "\\" + OCTAL * 3 or X + "\\0" }
    val Digit by lexer { DIGIT }
    val CharClass by lexer { X + "\\" + X["SsDdWwBb"] or X[".^\$"] }

    init {
        lexer.token { X[REG_OPERATORS] }
    }

    val Char by lexer { ANY }

    val branch: Parser by parser { X + piece.more() }
    val regex by parser { X + (X + branch).join(X + "|") }
    val specChar by parser { X + EscapeChar or X + Unicode or X + Hex or X + Octal or X + Digit or X + Char }
    val char by parser { X + CharClass or X + specChar }
    val item by parser { X + specChar + "-" + specChar or X + char }
    val atom by parser {
        X + char or
                X + "(" + "?" + "=" + regex + ")" or
                X + "(" + "?" + "!" + regex + ")" or
                X + "(" + regex + ")" or
                X + "[" + (X + "^").optional() + item.more() + "]"
    }
    val digits by parser { X + (X + Digit).more() }
    val quantity by parser { X + digits + "," + (X + digits).optional() or X + digits }
    val quantifier by parser { (X + "+" or X + "*" or X + "{" + quantity + "}") + (X + "?").optional() }
    val piece by parser { X + atom + quantifier + branch.optional() or X + atom + (X + "?").optional() }

    companion object {
        const val REG_OPERATORS = "\\()[]{}|.-?+*^\$"
    }
}
