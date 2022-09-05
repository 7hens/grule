package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException


class LexerMatcherException(val status: LexerMatcherStatus, val rule: Any) :
    MatcherException("Expect '$rule' , actual is '${status.peek()}' at ${status.position} ")
            
