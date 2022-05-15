package io.grule.lexer

open class LexerException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(context: LexerContext, expect: String, actual: String)
            : this("Expect '$expect' at ${context.position}, actual is '$actual'")

    constructor(context: LexerContext, expect: Lexer, actual: Lexer)
            : this(context, expect.toString(), actual.toString())
}