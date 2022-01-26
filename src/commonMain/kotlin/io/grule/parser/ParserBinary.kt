package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserBinary(val item: Parser, val operator: Parser, val comparator: Comparator<AstNode>): Parser() {
    override val isFlatten get() = true

    init {
        require(!item.isFlatten) { "item should be a single parser" }
        require(!operator.isFlatten) { "operator should be a single parser" }
    }

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        //  item
        //  |-- item
        //  |-- operator
        //  '-- item
        var prevNode = AstNode(this)
        var result = item.tryParse(tokenStream, offset, prevNode)
        while (true) {
            val nextNode = AstNode(this)
            try {
                result += operator.tryParse(tokenStream, offset + result, nextNode)
                result += item.tryParse(tokenStream, offset + result, nextNode)
            } catch (e: Throwable) {
                break
            }
            prevNode = mergeNode(prevNode, nextNode)
        }
        parentNode.add(prevNode)
        return result
    }

    private fun mergeNode(prev: AstNode, next: AstNode): AstNode {
        val nextOperator = next.first(operator)
        if (operator !in prev) {
            val parentNode = AstNode(this)
            parentNode.add(prev)
            parentNode.add(nextOperator)
            parentNode.add(wrap(next))
            return parentNode
        }
        val prevOperator = prev.first(operator)
        if (comparator.compare(prevOperator, nextOperator) >= 0) {
            val node = AstNode(this)
            node.add(prev)
            node.add(nextOperator)
            node.add(wrap(next))
            return node
        }

        val prevLastItem = prev.last(this)
        prev.remove(prevLastItem)
        prev.add(mergeNode(prevLastItem, next))
        return prev
    }

    private fun wrap(node: AstNode): AstNode {
        val wrapper = AstNode(this)
        wrapper.add(node.first(item))
        return wrapper
    }
}