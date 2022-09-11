package io.grule.node

class AstNodeChain private constructor(private val chain: List<AstNode>) {
    val root: AstNode get() = chain.first()

    val parent: AstNode get() = chain[chain.lastIndex - 1]

    val me: AstNode get() = chain.last()

    fun pop(count: Int = 1): AstNodeChain {
        return AstNodeChain(chain.subList(0, chain.size - count))
    }

    fun add(child: AstNode): AstNodeChain {
        me.add(child)
        return AstNodeChain(chain + child)
    }

    fun addHead(child: AstNode): AstNodeChain {
        me.addHead(child)
        return AstNodeChain(chain + child)
    }

    fun addParent(newNode: AstNode): AstNodeChain {
        newNode.add(me)
        parent.remove(me)
        parent.add(newNode)
        return AstNodeChain(pop().chain + newNode)
    }

    fun toFirstChild(): AstNodeChain {
        return AstNodeChain(pop().chain + me.first())
    }

    fun toLastChild(): AstNodeChain {
        return AstNodeChain(pop().chain + me.last())
    }

    fun forMe(fn: AstNode.() -> Unit): AstNodeChain {
        fn(me)
        return this
    }

    fun forParent(fn: AstNode.() -> Unit): AstNodeChain {
        fn(parent)
        return this
    }

    override fun toString(): String {
        return chain.joinToString(", ", "[", "]")
    }

    companion object {
        fun of(node: AstNode): AstNodeChain {
            return AstNodeChain(listOf(node))
        }
    }
}