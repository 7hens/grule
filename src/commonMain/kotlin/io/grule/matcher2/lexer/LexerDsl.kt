package io.grule.matcher2.lexer

object LexerDsl {
    val X = Builder()
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

    class Builder {
        operator fun plus(matcher: LexerMatcher): LexerMatcher {
            return matcher
        }

        operator fun plus(text: String): LexerMatcher {
            return plus(LexerMatcherString(text))
        }

        operator fun minus(charSet: Iterable<Char>): LexerMatcher {
            return plus(LexerMatcherCharSet(charSet))
        }

        operator fun minus(char: Char): LexerMatcher {
            return minus(listOf(char))
        }

        operator fun minus(charArray: CharArray): LexerMatcher {
            return minus(charArray.toList())
        }

        operator fun minus(text: String): LexerMatcher {
            return minus(text.toList())
        }
    }
}