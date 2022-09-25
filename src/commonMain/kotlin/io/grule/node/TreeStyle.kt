package io.grule.node

@Suppress("MemberVisibilityCanBePrivate")
class TreeStyle(
    val branch: String,
    val leaf: String,
    val wrap: String,
    val branchIndent: String,
    val leafIndent: String,
) {
    private val wrapReg = Regex.fromLiteral(wrap)

    fun applyTo(builder: StringBuilder, child: CharSequence, isLastChild: Boolean) {
        builder.append(wrap + if (isLastChild) leaf else branch)
        builder.append(child.replace(wrapReg, wrap + if (isLastChild) leafIndent else branchIndent))
    }

    companion object {
        val SOLID = TreeStyle(" ├─ ", " └─ ", "\n", " │  ", "    ")
        val DASHED = TreeStyle("|-- ", "'-- ", "\n", "|   ", "    ")
        val DOTED = TreeStyle(". ", ". ", "\n", ". ", "  ")
    }
}