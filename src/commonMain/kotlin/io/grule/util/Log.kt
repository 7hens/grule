package io.grule.util

interface Log {

    fun tag(tag: String): Log

    operator fun invoke(fn: () -> String): Log

    fun level(level: Level): Log

    val verbose: Log get() = level(Level.VERBOSE)

    val debug: Log get() = level(Level.DEBUG)

    val info: Log get() = level(Level.INFO)

    val warn: Log get() = level(Level.WARN)

    val error: Log get() = level(Level.ERROR)

    companion object : Log by Impl(Level.INFO, "") {
    }

    private class Impl(val level: Level, val tag: String) : Log {
        override fun tag(tag: String): Log {
            return Impl(level, tag)
        }

        override fun invoke(fn: () -> String): Log {
            println("[${level.name.first()}: $tag] - ${fn()}")
            return this
        }

        override fun level(level: Level): Log {
            return when {
                level == this.level -> this
                level > this.level -> Impl(level, tag)
                else -> Empty
            }
        }
    }

    private object Empty : Log {
        override fun tag(tag: String): Log = this
        override fun invoke(fn: () -> String): Log = this
        override fun level(level: Level): Log = this
    }

    enum class Level { VERBOSE, DEBUG, INFO, WARN, ERROR }
}