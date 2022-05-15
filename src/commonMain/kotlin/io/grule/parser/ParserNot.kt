package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserNot(val parser: Parser) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        try {
            parser.parse(tokenStream, offset, node)
        } catch (_: ParserException) {
            return 0
        }
        throw ParserException()
    }

    override fun not(): Parser {
        return parser
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || this.parser.isRecursive(parser)
    }
}