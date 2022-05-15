package io.grule.lexer

internal class ScannerRules : Scanner() {
    private val rules = mutableListOf<Scanner>()

    override fun scan(context: ScannerContext) {
        var matches = false
        for (rule in rules) {
            try {
                rule.scan(context)
                matches = true
                break
            } catch (_: LexerException) {
            }
        }
        if (!matches) {
            if (context.peek(0) == null) {
                context.emitEOF()
                return
            }
            throw LexerException(
                "Unmatched character (${context.peek(0)}) " +
                        "at #${context.position}"
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
