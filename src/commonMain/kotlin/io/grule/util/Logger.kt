package io.grule.util

interface Logger {

    fun tag(tag: String): Logger

    operator fun invoke(fn: () -> String): Logger

    fun level(level: Level): Logger

    val verbose: Logger get() = level(Level.VERBOSE)

    val debug: Logger get() = level(Level.DEBUG)

    val info: Logger get() = level(Level.INFO)

    val warn: Logger get() = level(Level.WARN)

    val error: Logger get() = level(Level.ERROR)

    fun on(condition: Boolean): Logger {
        if (condition) {
            return this
        }
        return Empty
    }

    companion object : Logger by Impl(Level.INFO, "") {
    }

    private class Impl(val level: Level, val tag: String) : Logger {
        override fun tag(tag: String): Logger {
            return Impl(level, tag)
        }

        override fun invoke(fn: () -> String): Logger {
            println("[${level.name.first()}__$tag] - ${fn()}")
            return this
        }

        override fun level(level: Level): Logger {
            return when {
                level == this.level -> this
                level > this.level -> Impl(level, tag)
                else -> Empty
            }
        }
    }

    private object Empty : Logger {
        override fun tag(tag: String): Logger = this
        override fun invoke(fn: () -> String): Logger = this
        override fun level(level: Level): Logger = this
    }

    enum class Level { VERBOSE, DEBUG, INFO, WARN, ERROR }
}