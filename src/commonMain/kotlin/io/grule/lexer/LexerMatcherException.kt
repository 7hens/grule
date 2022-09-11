package io.grule.lexer

import io.grule.matcher.MatcherException


class LexerMatcherException(val status: LexerMatcherStatus, val rule: Any) :
    MatcherException("Expect '$rule' , actual is '${status.peek()}' at ${status.position} ")
            
