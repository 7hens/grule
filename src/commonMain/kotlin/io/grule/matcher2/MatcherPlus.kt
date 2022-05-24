package io.grule.matcher2

internal class MatcherPlus<T>(val matchers: List<Matcher<T>>) : Matcher<T> {
    init {
        require(matchers.isNotEmpty())
    }

    override fun match(context: T, offset: Int): Int {
        var result = 0
        for (lexer in matchers) {
            result += lexer.match(context, offset + result)
        }
        return result
    }

    override fun toString(): String {
        return matchers.joinToString(" + ")
    }
}