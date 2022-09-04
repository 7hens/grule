package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

// val parser by p { v or it + x + it }
internal class ParserRecurse(override val key: Any, val fn: (Parser) -> Parser) : Parser {
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
        lazyInit
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
//        if (parser === this) {
//            return RecurseBuilder() + NonRecurseParserWrapper()
//        }
        if (parser is ParserShadow) {
            return RecurseBuilder() + NonRecurseParserWrapper()
        }
        return RecurseBuilder() + parser
    }

    override fun or(parser: Parser): Parser {
        return RecurseBuilder() + parser
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        lazyInit
        return parsePrimitives(tokenStream, parentNode, offset)
    }

    private fun parsePrimitives(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var result = 0
        var error: Throwable? = null
        for (parser in primitiveParsers) {
            try {
                val node = AstNode(key)
                result += parser.parse(tokenStream, node, offset + result)
                parentNode.add(node)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                flatSingleChildNode(parentNode)
                return result
            } catch (e: ParserException) {
                error = e
            }
        }
        throw error ?: ParserException()
    }

    private fun parseRecursive(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var result = 0
        for (parser in recursiveParsers) {
            try {
                result += parserRecursiveRight(parser, tokenStream, offset + result, parentNode)
                result += parseRecursive(tokenStream, offset + result, parentNode)
                return result
            } catch (_: ParserException) {
            }
        }
        return result
    }

    private fun parserRecursiveRight(parser: Parser, tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(key)
        val lastChild = parentNode.all().last()
        node.add(lastChild)
        val result = parser.parse(tokenStream, node, offset)
        parentNode.remove(lastChild)
        parentNode.add(node)
        return result
    }

    private fun parserRecursiveLeft(parser: Parser, tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(key)
        node.add(parentNode.copy())
        val result = parser.parse(tokenStream, node, offset)
        parentNode.clear()
        parentNode.merge(node)
        return result
    }

    private fun flatSingleChildNode(node: AstNode) {
        if (node.isEmpty() || node.key != key) {
            return
        }
        val firstChild = node.all().first()
        if (node.size == 1 && firstChild.key == node.key) {
            node.remove(firstChild)
            node.merge(firstChild)
        } else {
            node.forEach { flatSingleChildNode(it) }
        }
    }

    private fun isMatchedSelf(parentNode: AstNode): Boolean {
        if (parentNode.isNotEmpty()) {
            return parentNode.all().last().key == key
        }
        return parentNode.key == key
    }

    override fun toString(): String {
        return key.toString()
    }

    private inner class RecurseBuilder : ParserBuilder() {
        override fun plus(parser: Parser): Parser {
            if (delegate is NonRecurseParserWrapper && parser == this@ParserRecurse) {
                return super.plus(PrimitiveParser())
            }
            return super.plus(parser)
        }
    }

    private inner class NonRecurseParserWrapper : ParserWrapper() {
        override val base: Parser = this@ParserRecurse

        override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
            if (isMatchedSelf(parentNode)) {
                return 0
            }
            return parsePrimitives(tokenStream, parentNode, offset)
        }
    }

    private inner class PrimitiveParser : ParserWrapper() {
        override val base: Parser = this@ParserRecurse
        private val parser by lazy { ParserPlus(primitiveParsers) }

        override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
            val node = AstNode(key)
            val result = parser.parse(tokenStream, node, offset)
            parentNode.add(node)
            return result
        }
    }
}