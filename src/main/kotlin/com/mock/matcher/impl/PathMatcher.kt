package com.mock.matcher.impl

import com.mock.extensions.decodeUrl
import com.mock.matcher.RequestMatcher
import java.util.regex.Pattern

class PathMatcher(private val pattern: Pattern) : RequestMatcher {

    override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
        return path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.split("?")?.first()?.let {
            pattern.matcher(it).matches()
        } == true
    }
}