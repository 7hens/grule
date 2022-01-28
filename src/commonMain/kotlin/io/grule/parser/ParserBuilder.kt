package io.grule.parser

import io.grule.lexer.TokenStream
import kotlin.reflect.KProperty

internal open class ParserBuilder : Parser() {
    private var myParser: Parser = Shadow
    private var name: String = "$" + this::class.simpleName
        set(value) {
            field = value
            isNamed = true
        }
    private var isNamed = false

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(this)
        val result = myParser.parse(tokenStream, offset, node)
        if (isNamed) {
            parentNode.add(node)
        } else {
            parentNode.merge(node)
        }
        return result
    }

    override fun plus(parser: Parser): Parser {
        myParser = myParser.plus(parser)
        return this
    }

    override fun or(parser: Parser): Parser {
        myParser = myParser.or(parser)
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    object Shadow : Parser() {
        override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
            return 0
        }

        override fun plus(parser: Parser): Parser {
            return ParserPlus(mutableListOf(parser))
        }

        override fun or(parser: Parser): Parser {
            return ParserOr(mutableListOf(parser))
        }
    }
}