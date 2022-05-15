package io.grule.lexer

import io.grule.matcher.MatcherException

internal class LexerRules : Lexer() {
    private val rules = mutableListOf<Lexer>()

    override fun lex(context: LexerContext) {
        var matches = false
        for (rule in rules) {
            try {
                rule.lex(context)
                matches = true
                break
            } catch (_: MatcherException) {
            }
        }
        if (!matches) {
            if (context.peek(0) == null) {
                context.emitEOF()
                return
            }
            throw MatcherException(
                "Unmatched character (${context.peek(0)}) " +
                        "at #${context.position}"
            )
        }
    }

    fun add(lexer: Lexer) {
        rules.add(lexer)
    }

    fun addIndentRules(newLine: Lexer, indent: Lexer, dedent: Lexer) {
        add(LexerIndent(newLine, indent, dedent))
    }
}
