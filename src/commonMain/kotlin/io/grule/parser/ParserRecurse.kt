package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

// val parser by p { v or it + x + it }
internal class ParserRecurse(private val parentKey: Any, val fn: (Parser) -> Parser) : Parser {
    private val primitiveParsers = mutableListOf<Parser>()
    private val recursiveParsers = mutableListOf<Parser>()
    private val lazyInit by lazy { init() }
    private val recursiveException = IllegalArgumentException("Must have one independent parser at least")

    private fun init() {
        val parsers = getSubParsers()
        for (parser in parsers) {
            if (isRecurseParser(parser)) {
                recursiveParsers.add(parser)
            } else {
                primitiveParsers.add(parser)
            }
        }
        if (primitiveParsers.isEmpty()) {
            throw recursiveException
        }
    }

    fun degrade(): Parser {
        init()
        if (recursiveParsers.isNotEmpty()) {
            return this
        }
        if (primitiveParsers.size == 1) {
            val parser = primitiveParsers.first()
            if (parser is ParserShadow) {
                return ParserEmpty
            }
            return parser
        }
        return ParserOr(primitiveParsers)
    }

    private fun getSubParsers(): List<Parser> {
        val resultParser = fn(this).let {
            if (it is ParserBuilder) it.delegate else it
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

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        lazyInit
        if (isMatchedSelf(parentNode)) {
            return 0
        }
        var result = 0
        var error: Throwable? = null
        for (parser in primitiveParsers) {
            try {
                val node = AstNode(parentKey)
                result += parser.parse(tokenStream, node, offset + result)
                parentNode.merge(node)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                flatSingleChildNode(parentNode)
                return result
            } catch (e: ParserException) {
                error = e
            }
        }
        throw error ?: ParserException()
    }

    private fun flatSingleChildNode(node: AstNode) {
        if (node.isEmpty()) {
            return
        }
        val firstChild = node.all().first()
        if (node.size == 1 && firstChild.key == node.key) {
            node.remove(firstChild)
            node.merge(firstChild)
        }
    }

    private fun isMatchedSelf(parentNode: AstNode): Boolean {
        if (parentNode.isNotEmpty()) {
            return parentNode.all().last().key == parentKey
        }
        return false
    }

    private fun parseRecursive(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var result = 0
        for (parser in recursiveParsers) {
            try {
                val node = AstNode(parentKey)
                val lastChild = parentNode.all().last()
                node.add(lastChild)
                result += parser.parse(tokenStream, node, offset + result)
                parentNode.remove(lastChild)
                parentNode.add(node)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                return result
            } catch (_: ParserException) {
            }
        }
        return result
    }

    private inner class RecurseBuilder : ParserBuilder()

    override fun toString(): String {
        return parentKey.toString()
    }
}