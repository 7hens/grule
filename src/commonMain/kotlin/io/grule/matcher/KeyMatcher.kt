package io.grule.matcher

import io.grule.node.KeyProvider

interface KeyMatcher<T : Status<T>> : Matcher<T>, KeyProvider

