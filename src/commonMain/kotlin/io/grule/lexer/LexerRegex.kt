package io.grule.lexer

import io.grule.parser.AstNode

internal class LexerRegex(private val pattern: String) : Lexer() {
    private val g = RegexGrammar()
    private val lexer = parseRegex(pattern)

    override fun match(charStream: CharStream, offset: Int): Int {
        return lexer.match(charStream, offset)
    }

    private fun parseRegex(pattern: String): Lexer {
        val astNode = g.parse(g.regex, pattern)
        println(astNode.toStringTree())
        return parseRegex(astNode)
    }

    override fun toString(): String {
        return pattern
    }

    private fun parseRegex(regex: AstNode): Lexer {
        return regex.all(g.branch).lexerOr { parseBranch(it) }
    }

    private fun parseBranch(branch: AstNode): Lexer {
        return branch.all(g.piece).lexerPlus { parsePiece(it) }
    }

    private fun parsePiece(piece: AstNode): Lexer {
        val atom = parseAtom(piece.first(g.atom))
        return parseQuantifier(piece.firstOrNull(g.quantifier), atom)
    }

    private fun parseQuantifier(quantifier: AstNode?, lexer: Lexer): Lexer {
        if (quantifier == null) {
            return lexer
        }
        val quantity = quantifier.firstOrNull(g.quantity)
        if (quantity != null) {
            val digits = quantity.all(g.digits)
            val firstNum = parseDigits(digits.first())
            val lastNum = parseDigits(digits.last())
            return lexer.repeat(firstNum, lastNum)
        }
        val isOptional = quantifier.contains("?")
        val isStar = quantifier.contains("*")
        val isPlus = quantifier.contains("+")
        if (!isStar && !isPlus) {
            return lexer.optional(isOptional)
        }
        return lexer.repeat(if (isStar) 0 else 1)
    }

    private fun parseDigits(digits: AstNode): Int {
        return digits.text.toInt()
    }

    private fun parseAtom(atom: AstNode): Lexer {
        if (g.char in atom) {
            return LexerString(atom.all(g.char).joinToString("") { it.text })
        }
        atom.firstOrNull(g.regex)?.let { return parseRegex(it) }
        return atom.all(g.item).lexerOr { parseItem(it) }
    }

    private fun parseItem(item: AstNode): Lexer {
        val charList = item.all(g.char)
        val firstChar = charList.first().text
        val lastChar = charList.last().text
        if (charList.size == 1) {
            return LexerString(firstChar)
        }
        return LexerCharSet(firstChar[0]..lastChar[0])
    }

    private fun <T> List<T>.lexerOr(fn: (T) -> Lexer): Lexer {
        return fold(g.L) { acc, node -> acc.or(fn(node)) }
    }

    private fun <T> List<T>.lexerPlus(fn: (T) -> Lexer): Lexer {
        return fold(g.L) { acc, node -> acc.plus(fn(node)) }
    }
}
