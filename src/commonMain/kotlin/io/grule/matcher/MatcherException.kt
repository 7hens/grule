package io.grule.matcher

open class MatcherException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(context: Any?, offset: Int) : this("Unmatched item at $offset with $context")

    constructor(context: Any?) : this("Unmatched item with $context")
}