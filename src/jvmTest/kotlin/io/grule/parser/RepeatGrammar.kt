package io.grule.parser

import io.grule.Grammar

class RepeatGrammar : Grammar() {
    val N by lexer { DIGIT.more() }
    val O by lexer { X["+-*/%><=!"] }

    init {
        lexer.skip { WRAP or SPACE }
        lexer.token { X + "x" }
    }
}