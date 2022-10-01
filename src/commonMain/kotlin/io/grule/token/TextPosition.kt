package io.grule.token

data class TextPosition(val index: Int, val line: Int, val column: Int) {

    override fun toString(): String {
        return "$line:$column"
    }

    companion object {
        val Default = TextPosition(0, 0, 0)
    }
}
