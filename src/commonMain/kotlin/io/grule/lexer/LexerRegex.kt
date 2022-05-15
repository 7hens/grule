package io.grule.lexer

import io.grule.parser.AstNode

@Suppress("MoveVariableDeclarationIntoWhen")
internal class LexerRegex(private val pattern: String) : Lexer {
    private val g = RegexGrammar()
    private val lexer = parseRegex(pattern)

    override fun match(context: LexerContext, offset: Int): Int {
        return lexer.match(context, offset)
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
            piece.contains(g.quantifier) -> {
                val lastAtom = piece.lastOrNull(g.branch)?.let { parseBranch(it) }
                parseQuantifier(piece.first(g.quantifier), firstAtom, lastAtom)
            }
            piece.contains("?") -> firstAtom.optional()
            else -> firstAtom
        }
    }

    private fun parseQuantifier(quantifier: AstNode, lexer: Lexer, terminal: Lexer?): Lexer {
        val isGreedy = !quantifier.contains("?")
        var minTimes = 0
        var maxTimes = Int.MAX_VALUE

        val quantity = quantifier.firstOrNull(g.quantity)
        when {
            quantity != null -> {
                val digits = quantity.all(g.digits)
                minTimes = digits.first().text.toInt()
                maxTimes = digits.last().text.toInt()
            }
            quantifier.contains("+") -> minTimes = 1
            else -> Unit
        }
        return when {
            terminal == null -> lexer.repeat(minTimes, maxTimes)
            isGreedy -> lexer.untilGreedy(terminal, minTimes, maxTimes)
            else -> lexer.untilNonGreedy(terminal, minTimes, maxTimes)
        }
    }

    private fun parseAtom(atom: AstNode): Lexer {
        atom.firstOrNull(g.char)?.let { return parseChar(it) }
        atom.firstOrNull(g.regex)?.let {
            val regex = parseRegex(it)
            return when {
                atom.contains("=") -> regex.test()
                atom.contains("!") -> regex.not().test()
                else -> regex
            }
        }
        return atom.all(g.item).lexerOr { parseItem(it) }.let {
            if (atom.contains("^")) it.not() else it
        }
    }

    private fun parseChar(char: AstNode): Lexer {
        char.firstOrNull(g.specChar)?.let { return LexerString("" + parseSpecChar(it)) }
        return parseClassChar(char.first(g.CharClass))
    }

    private fun parseItem(item: AstNode): Lexer {
        item.firstOrNull(g.char)?.let { return parseChar(it) }
        val singleChars = item.all(g.specChar).map { parseSpecChar(it) }
        return LexerCharSet(singleChars[0]..singleChars[1])
    }

    private fun parseSpecChar(singleChar: AstNode): Char {
        singleChar.firstOrNull(g.EscapeChar)?.let { return parseEscapeChar(it) }
        singleChar.firstOrNull(g.Unicode)?.let { return parseHex(it) }
        singleChar.firstOrNull(g.Hex)?.let { return parseHex(it) }
        singleChar.firstOrNull(g.Octal)?.let { return parseOctal(it) }
        return singleChar.text[0]
    }

    private fun parseHex(hex: AstNode): Char {
        return hex.text.substring(2).toInt(16).toChar().toString()[0]
    }

    private fun parseOctal(octal: AstNode): Char {
        return octal.text.substring(1).toInt(8).toChar().toString()[0]
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
            "\\S" -> Lexer.SPACE.not()
            "\\s" -> Lexer.SPACE
            "\\D" -> Lexer.DIGIT.not()
            "\\d" -> Lexer.DIGIT
            "\\W" -> Lexer.WORD.not()
            "\\w" -> Lexer.WORD
            else -> Lexer.ANY
        }
    }

    private fun <T> List<T>.lexerOr(fn: (T) -> Lexer): Lexer {
        return fold(Lexer.X) { acc, node -> acc.or(fn(node)) }
    }

    private fun <T> List<T>.lexerPlus(fn: (T) -> Lexer): Lexer {
        return fold(Lexer.X) { acc, node -> acc.plus(fn(node)) }
    }
}
