package io.grule.parser

import io.grule.lexer.TokenStream

// val parser by p { v or it + x + it }
internal class ParserRecurse(val fn: (Parser) -> Parser) : Parser() {
    private val primitiveParsers = mutableListOf<Parser>()
    private val recursiveParsers = mutableListOf<Parser>()
    private val lazyInit by lazy { init() }

    private fun init() {
        val parsers = getSubParsers()
        for (parser in parsers) {
            if (isRecurseParser(parser)) {
                recursiveParsers.add(parser)
            } else {
                primitiveParsers.add(parser)
            }
        }
        if (parsers.all { this in it }) {
            throw IllegalArgumentException("Must have one dependent parser at least")
        }
    }

    private fun getSubParsers(): List<Parser> {
        val resultParser = fn(this).let {
            if (it is ParserBuilder) it.myParser else it
        }
        return if (resultParser is ParserOr) {
            resultParser.parsers
        } else {
            listOf(resultParser)
        }
    }
    private fun isRecurseParser(parser: Parser): Boolean {
        return parser is RecurseBuilder
    }

    override fun plus(parser: Parser): Parser {
        return RecurseBuilder() + parser
    }

    override fun or(parser: Parser): Parser {
        return RecurseBuilder() + parser
    }

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        lazyInit
        if (isMatchedSelf(parentNode)) {
            return 0
        }
        var result = 0
        var error: Throwable? = null
        for (parser in primitiveParsers) {
            try {
                val node = AstNode(this)
                result += parser.parse(tokenStream, offset + result, node)
                parentNode.add(node)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                return result
            } catch (e: ParserException) {
                error = e
            }
        }
        throw error ?: ParserException()
    }

    private fun isMatchedSelf(parentNode: AstNode): Boolean {
        if (parentNode.isNotEmpty()) {
            return parentNode.all().last().key == this
        }
        return false
    }

    private fun parseRecursive(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var result = 0
        for (parser in recursiveParsers) {
            try {
                val node = AstNode(this)
                val lastChild = parentNode.all().last()
                node.add(lastChild)
                result += parser.parse(tokenStream, offset + result, node)
                parentNode.remove(lastChild)
                parentNode.add(node)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                return result
            } catch (_: ParserException) {
            }
        }
        return result
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser
                || primitiveParsers.any { it.contains(parser) }
                || recursiveParsers.any { it.contains(parser) }
    }

    private inner class RecurseBuilder : ParserBuilder()
}