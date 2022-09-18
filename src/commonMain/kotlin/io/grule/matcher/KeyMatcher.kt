package io.grule.matcher

import io.grule.node.KeyOwner

interface KeyMatcher<T : Status<T>> : Matcher<T>, KeyOwner

