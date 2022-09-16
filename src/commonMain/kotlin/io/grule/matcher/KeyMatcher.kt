package io.grule.matcher

import io.grule.node.KeyProvider

interface KeyMatcher<T : Matcher.Status<T>> : Matcher<T>, KeyProvider

