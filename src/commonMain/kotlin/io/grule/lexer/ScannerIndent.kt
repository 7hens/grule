package io.grule.lexer

internal class ScannerIndent(val newLine: Scanner, val indent: Scanner, val dedent: Scanner) : Scanner() {
    private var prevTabCount = 0

    override fun scan(charStream: CharStream, tokenStream: TokenStream) {
        if (!scanEOF(charStream, tokenStream)) {
            val offset = L_wrap.match(charStream)
            val spaceNum = (L + "    ").repeat().match(charStream, offset)
            onIndent(tokenStream, spaceNum)
            charStream.moveNext(offset + spaceNum)
        }
    }

    private fun scanEOF(charStream: CharStream, tokenStream: TokenStream): Boolean {
        return try {
            Lexer.EOF.match(charStream)
            onIndent(tokenStream, -1)
            true
        } catch (e: LexerException) {
            false
        }
    }

    override fun toString(): String {
        return "<INDENT>"
    }

    private fun onIndent(tokenStream: TokenStream, spaceNum: Int) {
        val tabCount = spaceNum / 4
//        println(">> tab: $prevTabCount -> $tabCount")
        if (tabCount > prevTabCount) {
            for (i in prevTabCount until tabCount) {
                tokenStream.emit(indent, "::{")
            }
        } else {
            for (i in tabCount until prevTabCount) {
                tokenStream.emit(dedent, "}::")
            }
            if (spaceNum >= 0) {
                tokenStream.emit(newLine, "\\n")
            } else {
                tokenStream.emitEOF()
            }
        }
        prevTabCount = tabCount
    }
}