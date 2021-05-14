package com.mock.matcher

infix fun RequestMatcher.or(target: RequestMatcher): RequestMatcher = { p, b -> invoke(p, b) || target.invoke(p, b) }

infix fun RequestMatcher.and(target: RequestMatcher): RequestMatcher = { p, b -> invoke(p, b) && target.invoke(p, b) }
