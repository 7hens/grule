package io.grule.matcher2.lexer

import io.grule.matcher.CharReader
import org.junit.Test

/**
 * LexerMatcherKtTest.
 * @author huangzhenzi
 */
internal class LexerDslTest {

    @Test
    fun plus() {
        val lexer = LexerDsl { X + "A" or X + "BC" self { me + it } }
        val result = lexer.match("ABCABCABC")
        println(result)
    }

    private fun LexerMatcher.match(text: String): LexerMatcherContext {
        val charStream = CharReader.fromString(text).toStream()
        val context = LexerMatcherContext.from(charStream)
        return match(context)
    }
}