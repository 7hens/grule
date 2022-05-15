package io.grule.lexer

interface CharStream: LexerContext {
    
    fun moveNext(count: Int)
    
    companion object {
        fun fromString(text: String): CharStream {
            return CharReader.fromString(text).toStream()
        }
    }
}
