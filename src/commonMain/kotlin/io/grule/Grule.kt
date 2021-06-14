package io.grule

import io.grule.lexer.*
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder

@Suppress("PropertyName", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class Grule : Scanner {
    private val rules = ScannerRules()

    val L: Lexer get() = LexerBuilder()
    val P: Parser get() = ParserBuilder()

    val ANY get() = L + LexerCharSet.ANY
    val DIGIT get() = L + ('0'..'9')
    val LETTER get() = L + ('A'..'Z') or L + ('a'..'z')
    val WORD get() = L + LETTER or DIGIT or L + '_'
    val SPACE get() = L + -" \t"
    val LINE get() = L + -"\r\n"
    val EOF get() = Lexer.EOF

    val TokenL: Lexer get() = L.also { addRule(it.token()) }
    val SkipL: Lexer get() = L.also { addRule(it.skip()) }

    private var scanners = mutableListOf<Scanner>(rules)

    override fun scan(channel: TokenChannel) {
        scanners.last().scan(channel)
    }

    fun push(scanner: Scanner) {
        if (scanner is Grule) {
            scanner.scanners = scanners
            scanners.add(scanner.rules)
        } else {
            scanners.add(scanner)
        }
    }

    fun pop() {
        scanners.removeLast()
    }

    fun addRule(lexer: Lexer) {
        rules.add(lexer)
    }

    fun addIndentRules(newLine: Lexer, indent: Lexer, dedent: Lexer) {
        rules.addIndentRules(newLine, indent, dedent)
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grule.() -> T): T {
            return fn(Grule())
        }
    }
}