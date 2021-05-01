package com.infeez.mock.dsl.context

import com.infeez.mock.dsl.context.MockMatcherContext.MockMatherRule.Body
import com.infeez.mock.dsl.context.MockMatcherContext.MockMatherRule.Param
import com.infeez.mock.dsl.context.MockMatcherContext.MockMatherRule.Path
import com.infeez.mock.matcher.BodyConverter.BodyDataConverter
import com.infeez.mock.matcher.BodyConverter.BodyString
import com.infeez.mock.matcher.BodyParamMather
import com.infeez.mock.matcher.PathMatcher
import com.infeez.mock.matcher.QueryParamMatcher
import com.infeez.mock.matcher.RequestMatcher
import java.util.regex.Pattern

class MockMatcherContext {

    fun path(init: Path.() -> RequestMatcher) = init(Path)

    val path = Path

    fun param(param: String, init: Param.() -> RequestMatcher) = init(Param(param))

    fun param(param: String) = Param(param)

    fun body(init: Body.() -> RequestMatcher) = init(Body)

    val body = Body

    sealed class MockMatherRule {
        object Path : MockMatherRule()
        data class Param(val name: String) : MockMatherRule()
        object Body : MockMatherRule() {
            inline fun <reified T> bodyMarch(noinline matcher: T.() -> Boolean) = BodyParamMather(matcher, BodyDataConverter(T::class.java))
            inline fun <reified T> bodyEq(src: String) = object : RequestMatcher {
                override fun invoke(path: String?, res: String?): Boolean {
                    return BodyDataConverter<T>(T::class.java).let {
                        it.convert(src) == it.convert(res!!)
                    }
                }
            }
        }

        fun any(): RequestMatcher = { _, _ -> true }

        fun eq(value: String) = matches(exact(value))

        fun startWith(value: String) = matches(prefix(value))

        fun endsWith(value: String) = matches(suffix(value))

        fun matches(pattern: Pattern) = when (this) {
            is Body -> BodyParamMather({ pattern.matcher(this).matches() }, BodyString)
            is Param -> QueryParamMatcher(name, pattern)
            is Path -> PathMatcher(pattern)
        }

        private fun exact(text: String) = Pattern.compile(Pattern.quote(text))
        private fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
        private fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
    }
}
