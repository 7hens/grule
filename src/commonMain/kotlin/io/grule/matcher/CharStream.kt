package io.grule.matcher

interface CharStream: MatcherContext {
    
    fun moveNext(count: Int)
    
    companion object {
        fun fromString(text: String): CharStream {
            return CharReader.fromString(text).toStream()
        }
    }
}
