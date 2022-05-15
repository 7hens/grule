package io.grule.lexer

interface ScannerContext : CharStream {
    fun emit(token: Token)

    fun emit(scanner: Scanner, text: String) {
        emit(Token(scanner, text, position))
    }

    fun emit(scanner: Scanner, count: Int) {
        emit(scanner, getText(0, count))
    }

    fun emit(scanner: Scanner) {
        emit(scanner, "<$scanner>")
    }

    fun emitEOF() {
        emit(Scanners.EOF)
    }
}
