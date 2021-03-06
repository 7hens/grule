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
            } catch (_: LexerException) {
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
        add(ScannerIndent(newLine, indent, dedent))
    }
}
