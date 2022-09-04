package io.grule.matcher2.lexer

object LexerMatcherDsl {
    val X = Builder()
    val any = X + LexerMatcherCharSet.ANY
    val eof = X + LexerMatcherEOF

    val digit = X - ('0'..'9')
    val bit = X - "01"
    val octal = X - ('0'..'7')
    val hex = X - "0123456789ABCDEFabcdef"

    val upper = X - ('A'..'Z')
    val lower = X - ('a'..'z')
    val letter = upper or lower

    val word = letter or digit or X - "_"
    val wordHead = letter or X - "_"
    val worldList = wordHead + word.repeat()

    val space = X - "\t\r\n\u0085\u000B\u000C "
    val wrap = X + "\r\n" or X - "\r\n"

    operator fun <T> invoke(fn: LexerMatcherDsl.() -> T): T = run(fn)

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