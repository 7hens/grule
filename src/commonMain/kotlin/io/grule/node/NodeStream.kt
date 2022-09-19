package io.grule.node

import io.grule.util.first
import io.grule.util.last

interface NodeStream<T> {

    fun transform(mapper: NodeMapper): T

    fun transform(mapper: AstNode.() -> AstNode): T {
        return transform(NodeMapper(mapper))
    }

    operator fun plus(node: AstNode): T {
        return transform { newNode(children + node) }
    }

    fun subList(fromIndex: Int, toIndex: Int): T {
        return transform { newNode(children.subList(fromIndex, toIndex)) }
    }

    fun subList(fromIndex: Int): T {
        return transform { newNode(children.subList(fromIndex, children.size)) }
    }

    fun first(): T {
        return transform { children.first() }
    }

    fun first(key: Any): T {
        return transform { map.first(key) }
    }

    fun last(): T {
        return transform { children.last() }
    }

    fun last(key: Any): T {
        return transform { map.last(key) }
    }

    fun wrapWith(keyOwner: KeyOwner): T {
        return transform { if (keyEquals(keyOwner)) this else keyOwner.newNode(this) }
    }

    fun flat(predicate: (AstNode) -> Boolean): T {
        return transform(NodeMapperFlat(predicate))
    }

    fun flatByKey(key: Any): T {
        return flat { it.keyEquals(key) }
    }

    fun binary(isOperator: (AstNode) -> Boolean, comparator: Comparator<AstNode>): T {
        return transform(NodeMapperBinary(isOperator, comparator))
    }

    fun binary(isOperator: (AstNode) -> Boolean): T {
        return binary(isOperator, DefaultOperatorPriority)
    }

    fun binary(operator: Any, comparator: Comparator<AstNode> = DefaultOperatorPriority): T {
        return binary({ it.keyEquals(operator) }, comparator)
    }

    fun forTree(consumer: (AstNode) -> Unit): T {
        return transform(NodeMapperForTree(consumer))
    }

    fun map(mapper: (AstNode) -> AstNode): T {
        return transform { newNode(children.map(mapper)) }
    }

    fun mapTree(mapper: (AstNode) -> AstNode): T {
        return transform(NodeMapperMapTree(mapper))
    }
}