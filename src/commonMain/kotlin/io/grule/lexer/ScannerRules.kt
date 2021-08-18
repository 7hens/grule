package io.grule.lexer

internal class ScannerRules : Scanner {
    private val rules = mutableListOf<Lexer>()

    override fun scan(channel: TokenChannel) {
        val input = channel.input
        var matches = false
        for (rule in rules) {
            try {
                val matchNum = rule.match(input, 0)
                rule.onMatch(channel, matchNum)
                matches = true
                break
            } catch (_: Throwable) {
            }
        }
        if (!matches) {
            if (input.peek(0) == CharStream.EOF) {
                channel.emitEOF()
                return
            }
            throw LexerException("Unmatched character (${input.peek(0).toChar()}) " +
                    "at #${input.line}:${input.column}")
        }
    }

    fun add(lexer: Lexer) {
        rules.add(lexer)
    }

    fun addIndentRules(newLine: Lexer, indent: Lexer, dedent: Lexer) {
        var prevTabCount = 0
        val indentAction: TokenChannel.(Int) -> Unit = { num ->
            val tabCount = (num - 1) / 4
//            println(">> tab: $prevTabCount -> $tabCount")
            if (tabCount > prevTabCount) {
                for (i in prevTabCount until tabCount) {
                    emit(indent, "::{")
                }
            } else {
                for (i in tabCount until prevTabCount) {
                    emit(dedent, "}::")
                }
                emit(newLine, "\\n")
            }
            prevTabCount = tabCount
            input.moveNext(num)
        }
        add(LexerBuilder() + "\n" + (LexerBuilder() + "    ").repeat() - indentAction)
        add(Lexer.EOF - {
            indentAction(0)
            emitEOF()
        })
    }
}
