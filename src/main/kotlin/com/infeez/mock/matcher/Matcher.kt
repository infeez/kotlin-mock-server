package com.infeez.mock.matcher

import com.infeez.mock.utils.extractQueryParams
import java.net.URLDecoder
import java.util.regex.Pattern
import okhttp3.mockwebserver.RecordedRequest

interface Matcher {
    fun matches(request: RecordedRequest): Boolean
}

class PathMatcher(private val pattern: Pattern) : Matcher {
    override fun matches(request: RecordedRequest): Boolean {
        val path = URLDecoder.decode(request.path, "utf-8")
        return path.split("?").first().let { pattern.matcher(it).matches() }
    }
}

class QueryParamMatcher(private val param: String, private val pattern: Pattern) : Matcher {
    override fun matches(request: RecordedRequest): Boolean {
        val path = URLDecoder.decode(request.path, "utf-8")
        val params = extractQueryParams(path)
        return params[param]?.let { pattern.matcher(it).matches() } == true
    }
}

object rulePath
data class ruleParam(val name: String)

infix fun rulePath.eq(url: String) = matches(exact(url))
infix fun rulePath.startWith(url: String) = matches(prefix(url))
infix fun rulePath.endsWith(url: String) = matches(suffix(url))
infix fun rulePath.matches(pattern: Pattern) = PathMatcher(pattern)
infix fun rulePath.matches(regex: Regex) = matches(regex.toPattern())

infix fun ruleParam.eq(value: String) = matches(exact(value))
infix fun ruleParam.startWith(value: String) = matches(prefix(value))
infix fun ruleParam.endsWith(value: String) = matches(suffix(value))
infix fun ruleParam.matches(pattern: Pattern) = QueryParamMatcher(name, pattern)
infix fun ruleParam.matches(regex: Regex) = matches(regex.toPattern())

infix fun Matcher.or(target: Matcher): Matcher {
    return object : Matcher {
        override fun matches(request: RecordedRequest): Boolean {
            return this@or.matches(request) || target.matches(request)
        }
    }
}

infix fun Matcher.and(target: Matcher): Matcher {
    return object : Matcher {
        override fun matches(request: RecordedRequest): Boolean {
            return this@and.matches(request) && target.matches(request)
        }
    }
}

fun any() = Pattern.compile(".*")
fun exact(text: String) = Pattern.compile(Pattern.quote(text))
fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
