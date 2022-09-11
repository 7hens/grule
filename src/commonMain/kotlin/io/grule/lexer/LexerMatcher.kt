package io.grule.lexer

import io.grule.matcher2.Matcher

typealias LexerMatcher = Matcher<LexerMatcherStatus>

typealias LexerSupplier = LexerMatcherDsl.() -> LexerMatcher
