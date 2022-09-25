package io.grule.lexer

import io.grule.matcher.Matcher

object LexerDsl : LexerMatcherExt {
    val X: LexerMatcher = Matcher.shadow()
    val ANY = X + LexerMatcherCharSet.ANY
    val EOF = X + LexerMatcherEOF

    val DIGIT = X - ('0'..'9')
    val BIT = X - "01"
    val OCTAL = X - ('0'..'7')
    val HEX = X - "0123456789ABCDEFabcdef"

    val UPPER = X - ('A'..'Z')
    val LOWER = X - ('a'..'z')
    val LETTER = UPPER or LOWER

    val WORD = LETTER or DIGIT or X - "_"
    val WORD_HEAD = LETTER or X - "_"
    val WORD_LIST = WORD_HEAD + WORD.repeat()

    val SPACE = X - "\t\r\n\u0085\u000B\u000C "
    val WRAP = X + "\r\n" or X - "\r\n"

    operator fun <T> invoke(fn: LexerDsl.() -> T): T = run(fn)
}