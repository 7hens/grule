package io.grule.matcher

import io.grule.node.AstNode

@Suppress("MoveVariableDeclarationIntoWhen")
internal class MatcherRegex(private val pattern: String) : Matcher {
    private val g = RegexGrammar()
    private val lexer = parseRegex(pattern)

    override fun match(context: MatcherContext, offset: Int): Int {
        return lexer.match(context, offset)
    }

    private fun parseRegex(pattern: String): Matcher {
        val astNode = g.regex.parse(g, pattern)
        return parseRegex(astNode)
    }

    override fun toString(): String {
        return pattern
    }

    private fun parseRegex(regex: AstNode): Matcher {
        return regex.all(g.branch).lexerOr { parseBranch(it) }
    }

    private fun parseBranch(branch: AstNode): Matcher {
        return branch.all(g.piece).lexerPlus { parsePiece(it) }
    }

    private fun parsePiece(piece: AstNode): Matcher {
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

    private fun parseQuantifier(quantifier: AstNode, matcher: Matcher, terminal: Matcher?): Matcher {
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
            terminal == null -> matcher.repeat(minTimes, maxTimes)
            isGreedy -> matcher.untilGreedy(terminal, minTimes, maxTimes)
            else -> matcher.untilNonGreedy(terminal, minTimes, maxTimes)
        }
    }

    private fun parseAtom(atom: AstNode): Matcher {
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

    private fun parseChar(char: AstNode): Matcher {
        char.firstOrNull(g.specChar)?.let { return MatcherString("" + parseSpecChar(it)) }
        return parseClassChar(char.first(g.CharClass))
    }

    private fun parseItem(item: AstNode): Matcher {
        item.firstOrNull(g.char)?.let { return parseChar(it) }
        val singleChars = item.all(g.specChar).map { parseSpecChar(it) }
        return MatcherCharSet(singleChars[0]..singleChars[1])
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

    private fun parseClassChar(classChar: AstNode): Matcher {
        val text = classChar.text
        return when (text) {
            "\\S" -> Matcher.SPACE.not()
            "\\s" -> Matcher.SPACE
            "\\D" -> Matcher.DIGIT.not()
            "\\d" -> Matcher.DIGIT
            "\\W" -> Matcher.WORD.not()
            "\\w" -> Matcher.WORD
            else -> Matcher.ANY
        }
    }

    private fun <T> List<T>.lexerOr(fn: (T) -> Matcher): Matcher {
        return fold(Matcher.X) { acc, node -> acc.or(fn(node)) }
    }

    private fun <T> List<T>.lexerPlus(fn: (T) -> Matcher): Matcher {
        return fold(Matcher.X) { acc, node -> acc.plus(fn(node)) }
    }
}
