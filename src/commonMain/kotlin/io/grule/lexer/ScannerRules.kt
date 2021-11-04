package io.grule.lexer

internal class ScannerRules : Scanner() {
    private val rules = mutableListOf<Scanner>()

    override fun scan(tokenStream: TokenStream) {
        val charStream = tokenStream.charStream
        var matches = false
        for (rule in rules) {
            try {
                rule.scan(tokenStream)
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
        val indentAction: TokenStream.(Int) -> Unit = { num ->
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
            charStream.moveNext(num)
        }
        add(Scanners.create(L + "\n" + (L + "    ").repeat(), indentAction))
        add(Scanners.create(Lexer.EOF) {
            indentAction(0)
            emitEOF()
        })
    }
}
