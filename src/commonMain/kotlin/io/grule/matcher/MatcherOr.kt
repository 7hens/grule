package io.grule.matcher

internal class MatcherOr(private val matchers: List<Matcher>) : Matcher {
    init {
        require(matchers.isNotEmpty())
    }
    
    override fun match(context: MatcherContext, offset: Int): Int {
        for (lexer in matchers) {
            try {
                return lexer.match(context, offset)
            } catch (_: MatcherException) {
                continue
            }
        }
        throw MatcherException(toString())
    }

    override fun or(matcher: Matcher): Matcher {
        return MatcherOr(matchers + matcher)
    }

    override fun toString(): String {
        return matchers.joinToString(" | ")
    }
}