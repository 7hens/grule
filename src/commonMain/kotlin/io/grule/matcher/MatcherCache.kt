package io.grule.matcher

internal class MatcherCache<T : Status<T>>(
    val matcher: Matcher<T>,
) : Matcher<T> {
    private var cache: Cache<T>? = null

    override fun match(status: T): T {
        cache?.apply {
            if (statusArg == status) {
                return result()
            }
        }
        try {
            val result = matcher.match(status)
            cache = Cache(status, result, null)
            return result
        } catch (e: MatcherException) {
            cache = Cache(status, null, e)
            throw e
        }
    }

    private val canMatchesEmpty by lazy { matcher.matchesEmpty() }

    override fun matchesEmpty(): Boolean {
        return canMatchesEmpty
    }

    override fun cache(): Matcher<T> {
        return this
    }

    override fun toString(): String {
        return matcher.toString()
    }

    class Cache<T : Status<T>>(
        val statusArg: T,
        val statusResult: T?,
        val error: MatcherException?,
    ) {
        fun result(): T {
            if (statusResult != null) {
                return statusResult
            }
            throw error!!
        }
    }
}