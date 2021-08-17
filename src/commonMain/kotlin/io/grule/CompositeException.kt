package io.grule

class CompositeException(vararg errors: Throwable) : RuntimeException() {
    private val errorList = mutableSetOf<Throwable>()

    init {
        errorList.addAll(errors)
    }

    fun add(throwable: Throwable) {
        if (throwable == this) {
            return
        }
        errorList.add(throwable)
    }

    fun remove(throwable: Throwable) {
        errorList.remove(throwable)
    }

    fun isEmpty(): Boolean {
        return errorList.isEmpty()
    }

    fun get(): Throwable {
        if (errorList.size == 1) {
            return errorList.first()
        }
        return this
    }

    override val message: String
        get() = errorList.joinToString("", ">>>>>>>>\n", "\n<<<<<<<<") { e ->
            "\n\t* " + e.stackTraceToString().lineSequence().joinToString("\n\t\t")
        }

}