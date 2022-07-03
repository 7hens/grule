package io.grule.parser

interface AstNodeStream<T> {

    fun node(mapper: AstNode.Mapper): T

    fun flat(predicate: AstNode.Predicate): T {
        return node(AstNodeFlat(predicate))
    }

    fun binary(isOperator: AstNode.Predicate, comparator: Comparator<AstNode>): T {
        return node(AstNodeBinary(isOperator, comparator))
    }

    fun binary(isOperator: AstNode.Predicate): T {
        return binary(isOperator, AstNode.DefaultComparator)
    }

    fun each(consumer: AstNode.Consumer): T {
        return node(AstNodeEach(consumer))
    }
}