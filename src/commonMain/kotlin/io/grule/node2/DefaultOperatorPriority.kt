package io.grule.node2

enum class DefaultOperatorPriority {
    MIN, SEMICOLON, QUERY, OR, XOR, AND, EQ, REL, PLUS, TIMES, MAX;

    companion object : Comparator<Node> {
        private val priorities = mapOf(
            ";" to SEMICOLON,
            "?" to QUERY,
            "|" to OR,
            "^" to XOR,
            "&" to AND,
            "=" to EQ, "!" to EQ,
            ">" to REL, "<" to REL,
            "+" to PLUS, "-" to PLUS,
            "*" to TIMES, "/" to TIMES, "%" to TIMES,
        )

        fun of(operator: String): DefaultOperatorPriority {
            val first = operator.substring(0, 1)
            return priorities[first] ?: MIN
        }

        override fun compare(a: Node, b: Node): Int {
            val firstPriority = DefaultOperatorPriority.of(a.tokens.first().text)
            val secondPriority = DefaultOperatorPriority.of(b.tokens.first().text)
            return firstPriority.ordinal - secondPriority.ordinal
        }
    }
}