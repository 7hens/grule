package io.grule.node2

import io.grule.node.KeyOwner

interface NodeStream<T> {

    fun transform(mapper: NodeMapper): T

    fun transform(mapper: Node.() -> Node): T {
        return transform(NodeMapper(mapper))
    }

    operator fun plus(node: Node): T {
        return transform { newTree(list + node) }
    }

    fun subList(fromIndex: Int, toIndex: Int): T {
        return transform { newTree(list.subList(fromIndex, toIndex)) }
    }

    fun subList(fromIndex: Int): T {
        return transform { newTree(list.subList(fromIndex, list.lastIndex)) }
    }

    fun wrapWith(keyOwner: KeyOwner): T {
        return transform { if (keyEquals(keyOwner)) this else keyOwner.newTree(this) }
    }

    fun flat(predicate: (Node) -> Boolean): T {
        return transform(NodeMapperFlat(predicate))
    }

    fun flatByKey(key: Any): T {
        return flat { it.keyEquals(key) }
    }

    fun binary(isOperator: (Node) -> Boolean, comparator: Comparator<Node>): T {
        return transform(NodeMapperBinary(isOperator, comparator))
    }

    fun binary(isOperator: (Node) -> Boolean): T {
        return binary(isOperator, DefaultOperatorPriority)
    }

    fun binary(operator: Any, comparator: Comparator<Node> = DefaultOperatorPriority): T {
        return binary({ it.keyEquals(operator) }, comparator)
    }

    fun forTree(consumer: (Node) -> Unit): T {
        return transform(NodeMapperForTree(consumer))
    }

    fun map(mapper: (Node) -> Node): T {
        return transform { newTree(list.map(mapper)) }
    }

    fun mapTree(mapper: (Node) -> Node): T {
        return transform(NodeMapperMapTree(mapper))
    }
}