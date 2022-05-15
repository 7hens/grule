package io.grule.matcher

data class TextPosition(val index: Int, val line: Int, val column: Int) {
    override fun toString(): String {
        return "$line:$column"
    }
}
