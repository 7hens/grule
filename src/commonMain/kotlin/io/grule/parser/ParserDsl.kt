package io.grule.parser

import io.grule.matcher.Matcher

object ParserDsl : ParserMatcherExt {
    val X: ParserMatcher = Matcher.shadow()

    operator fun <T> invoke(fn: ParserDsl.() -> T): T = run(fn)
}