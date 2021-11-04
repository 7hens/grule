package io.grule.lexer

internal class ScannerRules : Scanner() {
    private val rules = mutableListOf<Scanner>()

    override fun scan(charStream: CharStream, tokenStream: TokenStream) {
        var matches = false
        for (rule in rules) {
            try {
                rule.scan(charStream, tokenStream)
                matches = true
                break
            } catch (_: Throwable) {
            }
        }
        if (!matches) {
            if (charStream.peek(0) == CharStream.EOF) {
                tokenStream.emitEOF()
                return
            }
            throw LexerException(
                "Unmatched character (${charStream.peek(0).toChar()}) " +
                        "at #${charStream.line}:${charStream.column}"
            )
        }
    }

    fun add(scanner: Scanner) {
        rules.add(scanner)
    }

    fun addIndentRules(newLine: Scanner, indent: Scanner, dedent: Scanner) {
        var prevTabCount = 0
        val indentAction = { charStream: CharStream, tokenStream: TokenStream, matchNum: Int ->
            val tabCount = (matchNum - 1) / 4
//            println(">> tab: $prevTabCount -> $tabCount")
            if (tabCount > prevTabCount) {
                for (i in prevTabCount until tabCount) {
                    tokenStream.emit(indent, "::{")
                }
            } else {
                for (i in tabCount until prevTabCount) {
                    tokenStream.emit(dedent, "}::")
                }
                tokenStream.emit(newLine, "\\n")
            }
            prevTabCount = tabCount
            charStream.moveNext(matchNum)
        }
        add(object : Scanner() {
            override fun scan(charStream: CharStream, tokenStream: TokenStream) {
                val lexer = L + "\n" + (L + "    ").repeat()
                val matchNum = lexer.match(charStream)
                indentAction(charStream, tokenStream, matchNum)
            }
        })
        add(object : Scanner() {
            override fun scan(charStream: CharStream, tokenStream: TokenStream) {
                Lexer.EOF.match(charStream)
                indentAction(charStream, tokenStream, 1)
                tokenStream.emitEOF()
            }
        })
    }
}
