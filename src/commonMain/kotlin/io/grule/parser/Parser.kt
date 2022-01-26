package io.grule.parser

import io.grule.lexer.Scanner
import io.grule.lexer.Scanners
import io.grule.lexer.TokenStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Parser : ReadOnlyProperty<Any?, Parser> {
    var name = "$" + this::class.simpleName
    private var isNamed = false

    open val isFlatten get() = !isNamed

    abstract fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int

    fun tryParse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        if (isFlatten) {
            return parse(tokenStream, offset, parentNode)
        }
        val node = AstNode(this)
        val result = parse(tokenStream, offset, node)
        parentNode.add(node)
        return result
    }

    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = ParserBuilder() + this + Scanners.EOF
        val node = AstNode(this)
        mainParser.parse(tokenStream, 0, node)
        return node.all(this).first()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        isNamed = true
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    open operator fun plus(parser: Parser): Parser {
        return ParserPlus(mutableListOf(this, parser))
    }

    operator fun plus(scanner: Scanner): Parser {
        return plus(ParserToken(TokenMatcher(scanner)))
    }

    operator fun plus(text: String): Parser {
        return plus(ParserToken(TokenMatcher(text)))
    }

    open infix fun or(parser: Parser): Parser {
        return ParserOr(mutableListOf(this, parser))
    }

    open fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        return ParserRepeat(this, minTimes, maxTimes)
    }

    fun optional(): Parser {
        return repeat(0, 1)
    }

    fun repeatWith(separator: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        return ParserBuilder() + this + (ParserBuilder() + separator + this).repeat(minTimes, maxTimes)
    }

    fun interlace(separator: Parser): Parser {
        return ParserBuilder() + separator.optional() + repeatWith(separator).optional() + separator.optional()
    }

    fun binary(operator: Parser, comparator: Comparator<AstNode> = AstNode.DefaultComparator): Parser {
        return ParserBinary(this, operator, comparator)
    }
}
