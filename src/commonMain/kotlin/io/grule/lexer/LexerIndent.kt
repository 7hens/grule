package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.MatcherException

internal class LexerIndent(val newLine: Lexer, val indent: Lexer, val dedent: Lexer) : Lexer {
    private var prevTabCount = 0

    override fun lex(context: LexerContext) {
        try {
            Matcher.EOF.match(context)
            onIndent(context, -1)
        } catch (_: MatcherException) {
            val offset = Matcher.WRAP.match(context)
            val spaceNum = (Matcher.X + "    ").repeat().match(context, offset)
            onIndent(context, spaceNum)
            context.moveNext(offset + spaceNum)
        }
    }

    private fun onIndent(context: LexerContext, spaceNum: Int) {
        val tabCount = spaceNum / 4
//        println(">> tab: $prevTabCount -> $tabCount")
        if (tabCount > prevTabCount) {
            for (i in prevTabCount until tabCount) {
                context.emit(indent)
            }
        } else {
            for (i in tabCount until prevTabCount) {
                context.emit(dedent)
            }
            if (spaceNum >= 0) {
                context.emit(newLine)
            } else {
                context.emitEOF()
            }
        }
        prevTabCount = tabCount
    }

    override fun toString(): String {
        return "INDENT"
    }
}