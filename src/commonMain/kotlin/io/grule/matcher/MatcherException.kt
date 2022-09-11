package io.grule.matcher

import io.grule.token.MatcherContext

open class MatcherException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(context: MatcherContext, expect: String, actual: String)
            : this("Expect '$expect' at ${context.position}, actual is '$actual'")

    constructor(context: MatcherContext, expect: Matcher, actual: Matcher)
            : this(context, expect.toString(), actual.toString())

    constructor(context: MatcherContext)
            : this("unmatched '${context.peek(0)}' at ${context.position}")
}