package io.github.infeez.kotlinmockserver.dsl.http.v2

import io.github.infeez.kotlinmockserver.converter.BodyConverter
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.BodyParamMather
import io.github.infeez.kotlinmockserver.matcher.impl.HeaderParamMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.PathMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.QueryParamMatcher
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import java.util.regex.Pattern

fun pathEq(path: String): PathMatcher {
    return PathMatcher(exact(path))
}

fun pathStartWith(path: String): PathMatcher {
    return PathMatcher(prefix(path))
}

fun pathEndsWith(path: String): PathMatcher {
    return PathMatcher(suffix(path))
}

fun queryEq(
    key: String,
    value: String
): QueryParamMatcher {
    return QueryParamMatcher(key, exact(value))
}

fun queryStartWith(
    key: String,
    value: String
): QueryParamMatcher {
    return QueryParamMatcher(key, prefix(value))
}

fun queryEndsWith(
    key: String,
    value: String
): QueryParamMatcher {
    return QueryParamMatcher(key, suffix(value))
}

fun headerEq(
    key: String,
    value: String
): HeaderParamMatcher {
    return HeaderParamMatcher(key, exact(value))
}

fun headerStartWith(
    key: String,
    value: String
): HeaderParamMatcher {
    return HeaderParamMatcher(key, prefix(value))
}

fun headerEndsWith(
    key: String,
    value: String
): HeaderParamMatcher {
    return HeaderParamMatcher(key, suffix(value))
}

inline fun <reified T> bodyMatch(noinline matcher: T.() -> Boolean): BodyParamMather<T> {
    return BodyParamMather(
        matcher = matcher,
        bodyConverter = BodyConverter.BodyDataConverter(MockConfiguration.converterFactory!!, T::class.java)
    )
}

inline fun <reified T> bodyEq(src: String) = object : RequestMatcher {
    override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
        return BodyConverter.BodyDataConverter<T>(MockConfiguration.converterFactory!!, T::class.java).let {
            it.convert(src) == it.convert(body!!)
        }
    }
}

private fun exact(text: String) = Pattern.compile(Pattern.quote(text))
private fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
private fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
