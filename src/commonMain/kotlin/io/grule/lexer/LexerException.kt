package io.grule.lexer

open class LexerException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(charStream: CharStream, expect: String, actual: String)
            : this("Expect '$expect' at ${charStream.position}, actual is '$actual'")

    constructor(charStream: CharStream, expect: Lexer, actual: Lexer)
            : this(charStream, expect.toString(), actual.toString())
}