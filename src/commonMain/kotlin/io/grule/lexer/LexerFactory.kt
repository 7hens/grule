package io.grule.lexer

import io.grule.matcher.Matcher

typealias LexerFactory = Matcher.Companion.() -> Lexer
