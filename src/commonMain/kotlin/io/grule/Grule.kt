package io.grule

import io.grule.lexer.*
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder

@Suppress("PropertyName", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class Grule : Scanner() {
    private val rules = ScannerRules()
    private val lazyParsers = mutableListOf<() -> Unit>()

    val L: Lexer get() = LexerBuilder()

    val P: Parser get() = ParserBuilder()

    fun P(fn: (Parser) -> Unit): Parser {
        val parser = P
        lazyParsers.add { fn(parser) }
        return parser
    }

    val ANY get() = L + LexerCharSet.ANY
    val DIGIT get() = L + ('0'..'9')
    val UPPER_CASE = L + ('A'..'Z')
    val LOWER_CASE = L + ('a'..'z')
    val LETTER get() = L + UPPER_CASE or LOWER_CASE
    val WORD get() = L + LETTER or DIGIT or L + '_'
    val SPACE get() = L + -" \t"
    val LINE get() = L + -"\r\n"
    val EOF get() = Lexer.EOF

    private var scanners = mutableListOf<Scanner>(rules)

    override fun scan(tokenStream: TokenStream) {
        lazyParsers.forEach { it() }
        lazyParsers.clear()
        scanners.last().scan(tokenStream)
    }

    fun push(scanner: Scanner) {
        scanners.add(scanner)
    }

    fun pop() {
        scanners.removeLast()
    }

    fun addRule(scanner: Scanner) {
        rules.add(scanner)
    }

    fun addIndentRules(newLine: Scanner, indent: Scanner, dedent: Scanner) {
        rules.addIndentRules(newLine, indent, dedent)
    }

    fun token(lexer: Lexer): Scanner {
        return Scanner.token(lexer).also { addRule(it) }
    }

    fun skip(lexer: Lexer): Scanner {
        return Scanner.skip(lexer).also { addRule(it) }
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grule.() -> T): T {
            return fn(Grule())
        }
    }
}