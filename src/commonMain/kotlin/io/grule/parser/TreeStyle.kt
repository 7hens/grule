package io.grule.parser

@Suppress("MemberVisibilityCanBePrivate")
class TreeStyle(
    val wrap: String,
    val leaf: String,
    val branch: String,
    val leafIndent: String,
    val branchIndent: String,
) {
    private val wrapReg = Regex.fromLiteral(wrap)

    fun applyTo(builder: StringBuilder, child: CharSequence, isLastChild: Boolean) {
        builder.append(wrap + if (isLastChild) leaf else branch)
        builder.append(child.replace(wrapReg, wrap + if (isLastChild) leafIndent else branchIndent))
    }

    companion object {
        val SOLID = TreeStyle("\n", " └─ ", " ├─ ", "    ", " │  ")
        val DASHED = TreeStyle("\n", "'-- ", "|-- ", "    ", "|   ")
        val DOTED = TreeStyle("\n", ". ", ". ", "  ", ". ")
    }
}