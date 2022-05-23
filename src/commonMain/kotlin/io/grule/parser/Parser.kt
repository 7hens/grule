package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.TokenStream

fun interface Parser {

    fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int

    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = this + Lexer.EOF
        val node = AstNode("<ROOT>")
        mainParser.parse(tokenStream, node, 0)
//        println(node.toStringTree())
        return node.first(this)
    }

    fun not(): Parser {
        return ParserNot(this)
    }

    operator fun plus(parser: Parser): Parser {
        return ParserPlus(mutableListOf(this, parser))
    }

    operator fun plus(lexer: Lexer): Parser {
        return plus(ParserLexer(lexer))
    }

    operator fun plus(text: String): Parser {
        return plus(ParserString(text))
    }

    infix fun or(parser: Parser): Parser {
        return ParserOr(mutableListOf(this, parser))
    }

    fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        return ParserRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        return (this + separator).untilGreedy(this, min, max)
    }

    fun optional(): Parser {
        return repeat(0, 1)
    }

    fun interlace(separator: Parser): Parser {
        return separator.optional() + join(separator) + separator.optional()
    }

    fun untilGreedy(terminal: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        return ParserUntilGreedy(this, terminal, minTimes, maxTimes)
    }

    fun untilNonGreedy(terminal: Parser, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Parser {
        return ParserUntilNonGreedy(this, terminal, minTimes, maxTimes)
    }

    fun test(): Parser {
        return ParserTest(this)
    }

    fun binary(operator: Any, comparator: Comparator<AstNode> = AstNode.DefaultComparator): Parser {
        return ParserBinary(this, operator, comparator)
    }

    companion object {
        val X: Parser get() = ParserShadow()

        fun factory(): ParserFactory {
            return ParserFactory()
        }
    }
}
