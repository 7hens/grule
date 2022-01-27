package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserBinary(val item: Parser, val operator: Parser, val comparator: Comparator<AstNode>): Parser() {
    init {
        require(item.isNamed) { "item should be a named parser" }
        require(operator.isNamed) { "operator should be a named parser" }
    }

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        //  item
        //  |-- item
        //  |-- operator
        //  '-- item
        val key = parentNode.key
        var prevNode = AstNode(key)
        var result = item.tryParse(tokenStream, offset, prevNode)
        while (true) {
            val nextNode = AstNode(key)
            try {
                result += operator.tryParse(tokenStream, offset + result, nextNode)
                result += item.tryParse(tokenStream, offset + result, nextNode)
            } catch (e: Throwable) {
                break
            }
            prevNode = mergeNode(prevNode, nextNode, key)
        }
        parentNode.merge(prevNode)
        return result
    }

    private fun mergeNode(prev: AstNode, next: AstNode, key: Any): AstNode {
        val nextOperator = next.first(operator)
        if (operator !in prev) {
            val parentNode = AstNode(key)
            parentNode.add(prev)
            parentNode.add(nextOperator)
            parentNode.add(wrap(next, key))
            return parentNode
        }
        val prevOperator = prev.first(operator)
        if (comparator.compare(prevOperator, nextOperator) >= 0) {
            val node = AstNode(key)
            node.add(prev)
            node.add(nextOperator)
            node.add(wrap(next, key))
            return node
        }

        val prevLastItem = prev.last(key)
        prev.remove(prevLastItem)
        prev.add(mergeNode(prevLastItem, next, key))
        return prev
    }

    private fun wrap(node: AstNode, key: Any): AstNode {
        val wrapper = AstNode(key)
        wrapper.add(node.first(item))
        return wrapper
    }
}