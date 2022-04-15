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
//        println(astNode.toStringTree())
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
        val firstAtom = parseAtom(piece.first(g.atom))
        return when {
            g.quantifier in piece -> {
                val lastAtom = piece.all(g.atom).getOrNull(1)?.let { parseAtom(it) }
                parseQuantifier(piece.first(g.quantifier), firstAtom, lastAtom)
            }
            "?" in piece -> firstAtom.optional()
            else -> firstAtom
        }
    }

    private fun parseQuantifier(quantifier: AstNode, firstAtom: Lexer, lastAtom: Lexer?): Lexer {
        val firstLexer = parseQuantifier(quantifier, firstAtom)
        return when {
            "?" in quantifier && lastAtom != null -> firstLexer.until(lastAtom)
            lastAtom != null -> firstLexer.unless(lastAtom)
            else -> firstLexer
        }
    }

    private fun parseQuantifier(quantifier: AstNode, firstAtom: Lexer): Lexer {
        val quantity = quantifier.firstOrNull(g.quantity)
        return when {
            quantity != null -> {
                val digits = quantity.all(g.digits)
                val firstNum = parseDigits(digits.first())
                val lastNum = parseDigits(digits.last())
                return firstAtom.repeat(firstNum, lastNum)
            }
            "*" in quantifier -> firstAtom.repeat(0)
            "+" in quantifier -> firstAtom.repeat(1)
            else -> firstAtom.optional()
        }
    }

    private fun parseDigits(digits: AstNode): Int {
        return digits.text.toInt()
    }

    private fun parseAtom(atom: AstNode): Lexer {
        if (g.char in atom) {
            return atom.all(g.char).lexerPlus { parseChar(it) }
        }
        atom.firstOrNull(g.regex)?.let { return parseRegex(it) }
        return atom.all(g.item).lexerOr { parseItem(it) }.let {
            if (atom.contains("^")) it.not() else it
        }
    }

    private fun parseChar(char: AstNode): Lexer {
        val escape = char.firstOrNull(g.escape)
        if (escape != null) {
            return parseEscape(escape)
        }
        val text = char.text
        if (text == ".") {
            return g.L_any
        }
        return LexerString(text)
    }

    private fun parseEscape(escape: AstNode): Lexer {
        escape.firstOrNull(g.EscapeOperator)?.let {
            return LexerString(it.text.substring(1))
        }
        escape.firstOrNull(g.EscapeChar)?.let {
            return parseEscapeChar(it.text.substring(1))
        }
        (escape.firstOrNull(g.Unicode) ?: escape.firstOrNull(g.Hex))?.let {
            return LexerString(it.text.substring(2).toInt(16).toChar().toString())
        }
        escape.firstOrNull(g.Octal)?.let {
            return LexerString(it.text.substring(1).toInt(8).toChar().toString())
        }
        throw IllegalArgumentException(escape.toString())
    }

    private fun parseEscapeChar(text: String): Lexer {
        when (text) {
            "a" -> return LexerString("\u0007")
            "b" -> return LexerString("\b")
            "t" -> return LexerString("\t")
            "r" -> return LexerString("\r")
            "v" -> return LexerString("\u000B")
            "f" -> return LexerString("\u000C")
            "n" -> return LexerString("\n")
            "e" -> return LexerString("\u001B")
            "S" -> return g.L_space.not()
            "s" -> return g.L_space
            "D" -> return g.L_digit.not()
            "d" -> return g.L_digit
            "W" -> return g.L_word.not()
            "w" -> return g.L_word
        }
        throw IllegalArgumentException(text)
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
