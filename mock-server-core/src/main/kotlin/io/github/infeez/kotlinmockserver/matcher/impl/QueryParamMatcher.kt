package io.github.infeez.kotlinmockserver.matcher.impl

import io.github.infeez.kotlinmockserver.extensions.decodeUrl
import io.github.infeez.kotlinmockserver.extensions.extractQueryParams
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import java.util.regex.Pattern

class QueryParamMatcher(
    private val param: String,
    private val pattern: Pattern
) : RequestMatcher {

    override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
        return path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.extractQueryParams()?.get(param)?.let {
            pattern.matcher(it).matches()
        } == true
    }
}