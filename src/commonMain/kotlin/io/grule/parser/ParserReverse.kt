package io.grule.parser

import io.grule.lexer.TokenStream

// val parser by p { v or it + v or v + it or it v it }
internal class ParserReverse(val fn: (Parser) -> Parser) : Parser() {
    private val parsers: List<Parser> by lazy {
        val resultParser = fn(this)
        if (resultParser is ParserOr) {
            resultParser.parsers
        } else {
            listOf(resultParser)
        }.also { list ->
            if (list.all { it.contains(this) }) {
                throw ParserException("Must have one dependent parser at least")
            }
        }
    }
    
    private var matchedParser: Parser? = null

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        var result = 0
        var error: Throwable = ParserException()
        if (matchedParser != null) {
            return 0
        }
        for (parser in parsers) {
            try {
                result += parser.parse(tokenStream, offset + result, node)
                matchedParser = parser
                result += parse(tokenStream, offset + result, node)
                return result
            } catch (e: Throwable) {
                error = e
            }
        }
        throw error
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser || parsers.any { it.contains(parser) }
    }
}