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

    fun repeatWith(separator: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        val suffix = ParserBuilder() + separator + this
        val parser = ParserBuilder() + this + suffix.repeat(maxOf(minTimes, 0), maxOf(maxTimes, 0))
        return if (minTimes == 0) parser.optional() else parser
    }

    fun interlace(separator: Parser): Parser {
        return ParserBuilder() + separator.optional() + this.repeatWith(separator) + separator.optional()
    }

    fun until(terminal: Parser, mode: UntilMode = UntilMode.GREEDY): Parser {
        return when (mode) {
            UntilMode.GREEDY -> ParserUntilGreedy(this, terminal)
            UntilMode.RELUCTANT -> ParserUntilReluctant(this, terminal)
        }
    }

    fun binary(
        left: Parser,
        right: Parser,
        mode: BinaryMode = BinaryMode.GREEDY_RIGHT,
        comparator: Comparator<AstNode> = AstNode.DefaultComparator
    ): Parser {
        return when (mode) {
            BinaryMode.GREEDY_LEFT -> ParserBinaryGreedyLeft(this, left, right, comparator)
            BinaryMode.GREEDY_RIGHT -> ParserBinaryGreedyRight(this, left, right, comparator)
            BinaryMode.RELUCTANT_LEFT -> ParserBinaryReluctantLeft(this, left, right, comparator)
        }
    }

    fun binary(item: Parser, comparator: Comparator<AstNode> = AstNode.DefaultComparator): Parser {
        return binary(item, item, BinaryMode.GREEDY_RIGHT, comparator)
    }

    enum class UntilMode { GREEDY, RELUCTANT }

    enum class BinaryMode { GREEDY_LEFT, RELUCTANT_LEFT, GREEDY_RIGHT }
}
