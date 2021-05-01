package com.infeez.mock.matcher

import com.infeez.mock.MockServerSettings
import java.util.regex.Pattern

object rulePath
data class ruleParam(val name: String)
object ruleBody

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

inline infix fun <reified T> ruleBody.withConverter(noinline matcher: T.() -> Boolean) = BodyParamMather(matcher, BodyConverter.BodyDataConverter(T::class.java))
inline infix fun <reified T> ruleBody.matchWithBody(src: String) = object : RequestMatcher {
    override fun invoke(path: String?, res: String?): Boolean {
        val f = MockServerSettings.converterFactory!!.from<T>(src, T::class.java)
        val s = MockServerSettings.converterFactory!!.from<T>(res!!, T::class.java)
        return f == s
    }
}

infix fun ruleBody.withString(matcher: String.() -> Boolean) = BodyParamMather(matcher, BodyConverter.BodyString)

fun any() = Pattern.compile(".*")
fun exact(text: String) = Pattern.compile(Pattern.quote(text))
fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
