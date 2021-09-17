package io.github.infeez.kotlinmockserver.matcher

/**
 * Infix method for a combination of matchers by AND condition.
 */
infix fun RequestMatcher.and(target: RequestMatcher): RequestMatcher = { p, b, h ->
    this(p, b, h) && target.invoke(p, b, h)
}

/**
 * Infix method for a combination of matchers by OR condition.
 */
infix fun RequestMatcher.or(target: RequestMatcher): RequestMatcher = { p, b, h ->
    this(p, b, h) || target.invoke(p, b, h)
}
