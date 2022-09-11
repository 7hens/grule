package io.grule.matcher2.parser

import io.grule.matcher2.MatcherException


class ParserMatcherException(val status: ParserMatcherStatus, val rule: Any) :
    MatcherException("Unmatched '$rule' in ${status.node.key}, actual is ${status.peek()}") {
}