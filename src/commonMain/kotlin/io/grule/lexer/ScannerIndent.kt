package io.grule.lexer

internal class ScannerIndent(val newLine: Scanner, val indent: Scanner, val dedent: Scanner) : Scanner() {
    private var prevTabCount = 0

    override fun scan(context: ScannerContext) {
        try {
            Lexer.EOF.match(context)
            onIndent(context, 0)
        } catch (_: LexerException) {
            val offset = Lexer.WRAP.match(context)
            val spaceNum = (Lexer.X + "    ").repeat().match(context, offset)
            onIndent(context, spaceNum)
            context.moveNext(offset + spaceNum)
        }
    }

    private fun onIndent(context: ScannerContext, spaceNum: Int) {
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