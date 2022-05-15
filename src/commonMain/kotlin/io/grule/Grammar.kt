package io.grule

import io.grule.lexer.*
import io.grule.matcher.CharStream
import io.grule.parser.AstNode
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder
import io.grule.parser.ParserRecurse

@Suppress("PropertyName", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class Grammar {
    val P: Parser get() = ParserBuilder()
    val lexer = Lexer.builder()

    fun p(fn: (Parser) -> Parser): Parser {
        return ParserRecurse(fn)
    }
    
    fun parse(parser: Parser, charStream: CharStream): AstNode {
        return parser.parse(lexer.tokenStream(charStream))
    }

    fun parse(parser: Parser, text: String): AstNode {
        return parse(parser, CharStream.fromString(text))
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grammar.() -> T): T {
            return fn(Grammar())
        }
    }
}

