package io.grule.lexer

import io.grule.matcher.CharStream
import io.grule.matcher.Matcher
import io.grule.matcher.MatcherCharSet
import io.grule.parser.AstNode
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder
import kotlin.properties.ReadOnlyProperty

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
interface Lexer {
    fun lex(context: LexerContext)

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    fun parse(parser: Parser, charStream: CharStream): AstNode {
        return parser.parse(tokenStream(charStream))
    }

    fun parse(parser: Parser, text: String): AstNode {
        return parse(parser, CharStream.fromString(text))
    }

    companion object {
        val EOF: Lexer = LexerEOF

        fun of(matcher: Matcher, emitsToken: Boolean = true): Lexer {
            return LexerMatcher(matcher, emitsToken)
        }

        fun indent(newLine: Lexer, indent: Lexer, dedent: Lexer): Lexer {
            return LexerIndent(newLine, indent, dedent)
        }
        
        fun builder(): LexerBuilder {
            return LexerBuilder()
        }
    }
}
