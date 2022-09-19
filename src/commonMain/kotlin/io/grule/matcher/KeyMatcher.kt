package io.grule.matcher

import io.grule.node2.KeyOwner

interface KeyMatcher<T : Status<T>> : Matcher<T>, KeyOwner

