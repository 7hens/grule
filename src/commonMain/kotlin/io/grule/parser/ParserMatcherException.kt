package io.grule.parser

import io.grule.matcher.MatcherException


class ParserMatcherException(val status: ParserMatcherStatus, val rule: Any) :
    MatcherException("Unmatched '$rule' in ${status.node.key}, actual is ${status.peek()}") {
}