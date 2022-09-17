package io.grule.matcher

internal data class CountRange(val min: Int, val max: Int) {
    init {
        require(min in 0..max)
    }

    val length: Int get() = max - min

    val isInfinite: Boolean get() = max == Int.MAX_VALUE

    val isEmpty: Boolean get() = max == 0

    val isSingle: Boolean get() = max == 1

    fun toIntRange(): IntRange {
        return min..max
    }

    operator fun plus(value: Int): CountRange {
        val min = maxOf(0, min + value)
        val max = if (isInfinite) max else maxOf(0, max + value)
        return CountRange(min, max)
    }

    operator fun minus(value: Int): CountRange {
        return this + -value
    }

    override fun toString(): String {
        val minText = if (min == 0) "" else "" + min
        val maxText = if (max == Int.MAX_VALUE) "" else "" + max
        return if (minText == maxText) minText else "$minText,$maxText"
    }

    companion object {
        val EMPTY = CountRange(0, 0)

        val INFINITE = CountRange(0, Int.MAX_VALUE)

        fun of(range: IntRange): CountRange {
            if (range.isEmpty()) {
                return EMPTY
            }
            return CountRange(range.first, range.last)
        }
    }
}