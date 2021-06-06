package io.github.infeez.kotlinmockserver.matcher

infix fun RequestMatcher.or(target: RequestMatcher): RequestMatcher = { p, b, h -> invoke(p, b, h) || target.invoke(p, b, h) }

infix fun RequestMatcher.and(target: RequestMatcher): RequestMatcher = { p, b, h -> invoke(p, b, h) && target.invoke(p, b, h) }