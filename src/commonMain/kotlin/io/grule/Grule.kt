package io.grule

import io.grule.lexer.*
import io.grule.parser.Parser
import io.grule.parser.ParserRecurse

@Suppress("PropertyName", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class Grule : Scanner() {
    private val rules = ScannerRules()

    fun p(fn: (Parser) -> Parser): Parser {
        return ParserRecurse(fn)
    }

    private var scanners = mutableListOf<Scanner>(rules)

    override fun scan(charStream: CharStream, tokenStream: TokenStream) {
        scanners.last().scan(charStream, tokenStream)
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
        return Scanners.token(lexer).also { addRule(it) }
    }

    fun skip(lexer: Lexer): Scanner {
        return Scanners.skip(lexer).also { addRule(it) }
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grule.() -> T): T {
            return fn(Grule())
        }
    }
}

