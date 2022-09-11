package io.grule.lexer

import io.grule.matcher.Matcher

typealias LexerMatcher = Matcher<LexerMatcherStatus>

typealias LexerSupplier = LexerMatcherDsl.() -> LexerMatcher
