package com.infeez.mock.matcher

import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.extensions.extractQueryParams
import java.util.regex.Pattern
import okhttp3.mockwebserver.RecordedRequest

typealias RequestMatcher = (request: RecordedRequest) -> Boolean

class PathMatcher(private val pattern: Pattern) : RequestMatcher {
    override fun invoke(request: RecordedRequest): Boolean {
        return request.path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.split("?")?.first()?.let {
            pattern.matcher(it).matches()
        } == true
    }
}

class QueryParamMatcher(private val param: String, private val pattern: Pattern) : RequestMatcher {
    override fun invoke(request: RecordedRequest): Boolean {
        return request.path?.decodeUrl().takeUnless { it.isNullOrEmpty() }?.extractQueryParams()?.get(param)?.let {
            pattern.matcher(it).matches()
        } == true
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

infix fun RequestMatcher.or(target: RequestMatcher): RequestMatcher = { request -> this@or.invoke(request) || target.invoke(request) }
infix fun RequestMatcher.and(target: RequestMatcher): RequestMatcher = { request -> this@and.invoke(request) && target.invoke(request) }

fun any() = Pattern.compile(".*")
fun exact(text: String) = Pattern.compile(Pattern.quote(text))
fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
