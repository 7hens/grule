//package io.grule.node
//
//interface AstNodeStream<T> {
//
//    fun transform(transformation: AstNode.Transformation): T
//
//    fun transform(mapper: (AstNode) -> AstNode): T {
//        return transform(AstNode.Transformation(mapper))
//    }
//
//    fun wrapWith(key: Any): T {
//        return transform { AstNode.of(key, it) }
//    }
//
//    fun flat(predicate: (AstNode) -> Boolean): T {
//        return transform(AstNodeFlat(predicate))
//    }
//
//    fun flatByKey(key: Any): T {
//        return flat { it.keyEquals(key) }
//    }
//
//    fun binary(isOperator: (AstNode) -> Boolean, comparator: Comparator<AstNode>): T {
//        return transform(AstNodeBinary(isOperator, comparator))
//    }
//
//    fun binary(isOperator: (AstNode) -> Boolean): T {
//        return binary(isOperator, AstNode.DefaultComparator)
//    }
//
//    fun binary(operator: Any, comparator: Comparator<AstNode> = AstNode.DefaultComparator): T {
//        return binary({ it.keyEquals(operator) }, comparator)
//    }
//
//    fun forEach(consumer: (AstNode) -> Unit): T {
//        return transform { it.all().forEach(consumer); it }
//    }
//
//    fun forTree(consumer: (AstNode) -> Unit): T {
//        return transform(AstNodeForTree(consumer))
//    }
//
//    fun map(mapper: (AstNode) -> AstNode): T {
//        return transform { AstNode.of(it.key, it.all().map(mapper)) }
//    }
//
//    fun mapTree(mapper: (AstNode) -> AstNode): T {
//        return transform(AstNodeMapTree(mapper))
//    }
//}