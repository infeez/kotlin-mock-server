package io.github.infeez.kotlinmockserver.matcher

typealias RequestMatcher = (path: String?, body: String?, headers: Map<String, String>) -> Boolean