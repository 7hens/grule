package io.grule.lexer

open class LexerException : RuntimeException {
    constructor() : super()

    constructor(message: String): super(message)

    constructor(cause: Throwable): super(cause)

    constructor(message: String, cause: Throwable): super(message, cause)
}