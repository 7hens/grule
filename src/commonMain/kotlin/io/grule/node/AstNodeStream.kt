package io.grule.node

interface AstNodeStream<T> {

    fun map(mapper: AstNode.Mapper): T

    fun flat(predicate: AstNode.Predicate): T {
        return map(AstNodeFlat(predicate))
    }

    fun flatByKey(key: Any): T {
        return flat { it.key == key }
    }

    fun binary(isOperator: AstNode.Predicate, comparator: Comparator<AstNode>): T {
        return map(AstNodeBinary(isOperator, comparator))
    }

    fun binary(isOperator: AstNode.Predicate): T {
        return binary(isOperator, AstNode.DefaultComparator)
    }

    fun onEach(consumer: AstNode.Consumer): T {
        return map(AstNodeOnEach(consumer))
    }

    fun mapEach(mapper: AstNode.Mapper): T {
        return map(AstNodeMapEach(mapper))
    }
}