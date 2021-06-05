package com.mock.matcher

typealias RequestMatcher = (path: String?, body: String?, headers: Map<String, String>) -> Boolean