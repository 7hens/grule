package io.grule.node

import io.grule.token.TextRange
import io.grule.token.Token
import io.grule.util.MultiMap

internal class AstNodeTerminal(override val key: Any, val token: Token) : AstNode {

    override val text: String get() = token.text

    override val tokens: Sequence<Token> get() = sequenceOf(token)

    override val textRange: TextRange get() = token.textRange

    override val children: List<AstNode>
        get() = emptyList()

    override val map: MultiMap<Any, AstNode>
        get() = emptyMap()

    override fun toString(): String {
        if (key is String) {
            return "$key"
        }
        return "$key(${toStringExp()})"
    }

    override fun toStringExp(): String {
        return text.replace("(", "\\(").replace(")", "\\)").replace("\n", "\\n")
    }

    override fun toStringTree(style: TreeStyle): String {
        return toString()
    }
}