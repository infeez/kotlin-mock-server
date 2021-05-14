package com.mock.matcher.impl

import com.mock.extensions.decodeUrl
import com.mock.extensions.extractQueryParams
import com.mock.matcher.RequestMatcher
import java.util.regex.Pattern

class QueryParamMatcher(private val param: String, private val pattern: Pattern) : RequestMatcher {

    override fun invoke(path: String?, body: String?): Boolean {
        return path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.extractQueryParams()?.get(param)?.let {
            pattern.matcher(it).matches()
        } == true
    }
}
