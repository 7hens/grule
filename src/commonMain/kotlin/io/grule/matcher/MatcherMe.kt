package io.grule.matcher

/**
 * a self { me + b }            -> a + b.repeat()
 * a self { me + b or me + c }  -> a + (b or c).repeat()
 *
 * a self { b + me }            -> b.repeat() + a
 * a self { b + me or c + me }  -> (b or c).repeat() + a
 *
 * a self { me + b or c + me }  -> c.repeat() + (a + b.repeat())
 * a self { c + me or me + b }  -> (c.repeat() + a) + b.repeat()
 *
 * a self { me + x + me }       -> (a + x).repeat() + a
 * a self { me + x + it }       -> a + (x + a).repeat()
 * a self { it + x + me }       -> (a + x).repeat() + a
 *
 * a self { me.repeat() + b }   -> a.repeat() + b.repeat()
 * a self { me + b } self { me + c }
 *                              -> a + b.repeat() + c.repeat()
 */
class MatcherMe<T : Status<T>>(
    val prefix: Matcher<T>,
    val primary: Matcher<T>,
    val postfix: Matcher<T>,
) : Matcher<T> by prefix.repeat() + primary + postfix.repeat() {

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        return MatcherMe(prefix, primary, postfix + matcher)
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        if (matcher is MatcherMe<T>) {
            return MatcherMe(
                prefix or matcher.prefix,
                primary or matcher.primary,
                postfix or matcher.postfix
            )
        }
        return super.or(matcher)
    }

    class SelfImpl<T : Status<T>>(
        override val it: Matcher<T>,
        override val me: Matcher<T>
    ) : Matcher.Self<T>
}