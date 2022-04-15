package io.grule.lexer

import io.grule.parser.AstNode

@Suppress("MoveVariableDeclarationIntoWhen")
internal class LexerRegex(private val pattern: String) : Lexer() {
    private val g = RegexGrammar()
    private val lexer = parseRegex(pattern)

    override fun match(charStream: CharStream, offset: Int): Int {
        return lexer.match(charStream, offset)
    }

    private fun parseRegex(pattern: String): Lexer {
        val astNode = g.parse(g.regex, pattern)
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
        atom.firstOrNull(g.char)?.let { return parseChar(it) }
        atom.firstOrNull(g.regex)?.let { return parseRegex(it) }
        return atom.all(g.item).lexerOr { parseItem(it) }.let {
            if (atom.contains("^")) it.not() else it
        }
    }

    private fun parseChar(char: AstNode): Lexer {
        char.firstOrNull(g.singleChar)?.let { return LexerString("" + parseSingleChar(it)) }
        return parseClassChar(char.first(g.CharClass))
    }

    private fun parseItem(item: AstNode): Lexer {
        val char = item.firstOrNull(g.char)
        if (char != null) {
            return parseChar(char)
        }
        val singleChars = item.all(g.singleChar).map { parseSingleChar(it) }
        return LexerCharSet(singleChars[0]..singleChars[1])
    }

    private fun parseSingleChar(singleChar: AstNode): Char {
        singleChar.firstOrNull(g.EscapeChar)?.let {
            return parseEscapeChar(it)
        }
        (singleChar.firstOrNull(g.Unicode) ?: singleChar.firstOrNull(g.Hex))?.let {
            return parseHex(it)
        }
        singleChar.firstOrNull(g.Octal)?.let {
            return parseOctal(it)
        }
        return singleChar.text[0]
    }

    private fun parseEscapeChar(escapeChar: AstNode): Char {
        val text = escapeChar.text
        return when (text) {
            "\\a" -> '\u0007'
            "\\t" -> '\t'
            "\\r" -> '\r'
            "\\v" -> '\u000B'
            "\\f" -> '\u000C'
            "\\n" -> '\n'
            "\\e" -> '\u001B'
            else -> text[1]
        }
    }

    private fun parseClassChar(classChar: AstNode): Lexer {
        val text = classChar.text
        return when (text) {
            "\\S" -> g.L_space.not()
            "\\s" -> g.L_space
            "\\D" -> g.L_digit.not()
            "\\d" -> g.L_digit
            "\\W" -> g.L_word.not()
            "\\w" -> g.L_word
            else -> g.L_any
        }
    }

    private fun parseHex(hex: AstNode): Char {
        return hex.text.substring(2).toInt(16).toChar().toString()[0]
    }

    private fun parseOctal(octal: AstNode): Char {
        return octal.text.substring(1).toInt(8).toChar().toString()[0]
    }

    private fun <T> List<T>.lexerOr(fn: (T) -> Lexer): Lexer {
        return fold(g.L) { acc, node -> acc.or(fn(node)) }
    }

    private fun <T> List<T>.lexerPlus(fn: (T) -> Lexer): Lexer {
        return fold(g.L) { acc, node -> acc.plus(fn(node)) }
    }
}
