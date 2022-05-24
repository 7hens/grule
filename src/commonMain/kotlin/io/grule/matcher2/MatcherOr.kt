package io.grule.matcher2

internal class MatcherOr<T>(val matchers: List<Matcher<T>>) : Matcher<T> {
    init {
        require(matchers.isNotEmpty())
    }

    override fun match(context: T, offset: Int): Int {
        for (lexer in matchers) {
            try {
                return lexer.match(context, offset)
            } catch (_: MatcherException) {
                continue
            }
        }
        throw MatcherException(context, offset)
    }
    
    override fun toString(): String {
        return matchers.joinToString(" | ")
    }
}