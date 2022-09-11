package io.grule.parser

import io.grule.matcher.Matcher

typealias ParserMatcher = Matcher<ParserMatcherStatus>

typealias ParserSupplier = ParserDsl.() -> ParserMatcher
