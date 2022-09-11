package io.grule.lexer

import io.grule.matcher.MatcherException
import io.grule.token.CharStream

internal class LexerIndent(val newLine: Lexer, val indent: Lexer, val dedent: Lexer) : Lexer {
    private var prevTabCount = 0

    override fun lex(context: LexerContext) {
        try {
            LexerMatcherDsl.EOF.match(context)
            onIndent(context, -1)
        } catch (_: MatcherException) {
            val offset = LexerMatcherDsl.WRAP.match(context)
            val spaceNum = LexerMatcherDsl.run { (X + "    ").repeat() }.match(context, offset)
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

    fun LexerMatcher.match(charStream: CharStream, offset: Int = 0): Int {
        val status = LexerMatcherStatus.from(charStream).next(offset)
        return status.apply(this).position - offset
    }

    override fun toString(): String {
        return "INDENT"
    }
}