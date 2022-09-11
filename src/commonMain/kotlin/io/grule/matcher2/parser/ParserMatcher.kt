package io.grule.matcher2.parser

import io.grule.matcher2.Matcher

typealias ParserMatcher = Matcher<ParserMatcherStatus>

typealias ParserSupplier = ParserMatcherDsl.() -> ParserMatcher
