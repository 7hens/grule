package io.grule.matcher

internal class MatcherPlus(private val matchers: List<Matcher>) : Matcher {
    init {
        require(matchers.isNotEmpty())
    }

    override fun match(context: MatcherContext, offset: Int): Int {
        var result = 0
        for (lexer in matchers) {
            result += lexer.match(context, offset + result)
        }
        return result
    }

    override fun plus(matcher: Matcher): Matcher {
        return MatcherPlus(matchers + matcher)
    }

    override fun toString(): String {
        return matchers.joinToString("  ")
    }
}