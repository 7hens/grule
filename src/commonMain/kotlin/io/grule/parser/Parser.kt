package io.grule.parser

import io.grule.lexer.Scanner
import io.grule.lexer.Scanners
import io.grule.lexer.TokenStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Parser : ReadOnlyProperty<Any?, Parser> {
    abstract fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int

    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = ParserBuilder() + this + Scanners.EOF
        val node = AstNode("<ROOT>")
        mainParser.parse(tokenStream, 0, node)
        return node.all(this).first()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        return this
    }

    override fun toString(): String {
        return "__" + this::class.simpleName
    }

    open fun not(): Parser {
        return ParserNot(this)
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
        require(minTimes >= 0)
        require(minTimes <= maxTimes)
        return ParserRepeat(this, minTimes, maxTimes)
    }

    fun optional(): Parser {
        return repeat(0, 1)
    }

    fun repeat(separator: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        val suffix = ParserBuilder() + separator + this
        val parser = ParserBuilder() + this + suffix.repeat(maxOf(minTimes, 0), maxOf(maxTimes, 0))
        return if (minTimes == 0) parser.optional() else parser
    }

    fun interlace(separator: Parser): Parser {
        return ParserBuilder() + separator.optional() + this.repeat(separator) + separator.optional()
    }

    fun unless(terminal: Parser): Parser {
        return ParserUnless(this, terminal)
    }

    fun until(terminal: Parser): Parser {
        return ParserUntil(this, terminal)
    }

    fun binary(operator: Any, comparator: Comparator<AstNode> = AstNode.DefaultComparator): Parser {
        return ParserBinary(this, operator, comparator)
    }
}
