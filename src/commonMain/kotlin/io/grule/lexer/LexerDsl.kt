package io.grule.lexer

import io.grule.matcher.Matcher

object LexerDsl : LexerMatcherExt {
    val X: LexerMatcher = Matcher.shadow()
    val ANY = X + LexerMatcherCharSet.ANY
    val BOT = X + LexerMatcherbot // beginning of token
    val EOF = X + LexerMatcherEof // end of file

    val DIGIT = X - ('0'..'9')
    val BIT = X - "01"
    val OCTAL = X - ('0'..'7')
    val HEX = X - "0123456789ABCDEFabcdef"

    val UPPER = X - ('A'..'Z')
    val LOWER = X - ('a'..'z')
    val ALPHABET = UPPER or LOWER

    @Deprecated("Used ALPHABET instead", ReplaceWith("ALPHABET"))
    val LETTER = ALPHABET

    val ALPHANUMERIC = ALPHABET or DIGIT
    val WORD = ALPHANUMERIC or X - '_'
    val INITIAL = ALPHABET or X - '_'
    val IDENTIFIER = INITIAL + WORD.repeat()

    @Deprecated("Used INITIAL instead", ReplaceWith("INITIAL"))
    val WORD_HEAD = INITIAL

    @Deprecated("Used IDENTIFIER instead", ReplaceWith("IDENTIFIER"))
    val WORD_LIST = IDENTIFIER

    val BOUNDARY = BOT or EOF or X - 1 + (WORD + WORD.not() or WORD.not() + WORD) - 1

    val SPACE = X - "\t\r\n\u0085\u000B\u000C "
    val WRAP = X + "\r\n" or X - "\r\n"
    val BOL = BOT or X - 2 + "\r\n" or X - 1 - "\r\n"  // beginning of line
    val EOL = EOF or X + WRAP.test()                   // end of line

    operator fun <T> invoke(fn: LexerDsl.() -> T): T = run(fn)
}