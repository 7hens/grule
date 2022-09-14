package io.grule.parser

import io.grule.Grammar

class RepeatGrammar : Grammar() {
    val Num by lexer { DIGIT.more() }
    val Op by lexer { X - "+-*/%><=!" }

    init {
        lexer.skip { WRAP or SPACE }
        lexer.token { X + "x" }
    }
}