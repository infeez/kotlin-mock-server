package com.mock.dsl.http.context

import com.mock.MockServerSettings
import com.mock.converter.BodyConverter.BodyDataConverter
import com.mock.converter.BodyConverter.BodyString
import com.mock.dsl.http.context.MockMatcherContext.MockMatherRule.Body
import com.mock.dsl.http.context.MockMatcherContext.MockMatherRule.Param
import com.mock.dsl.http.context.MockMatcherContext.MockMatherRule.Path
import com.mock.matcher.RequestMatcher
import com.mock.matcher.impl.BodyParamMather
import com.mock.matcher.impl.PathMatcher
import com.mock.matcher.impl.QueryParamMatcher
import java.util.regex.Pattern

/**
 * This class realise DSL-context to build url matcher.
 *
 */
class MockMatcherContext {

    /**
     * Matcher for url path. With [Path]-context.
     *
     */
    fun path(matcher: Path.() -> RequestMatcher) = matcher(Path)

    val path = Path

    fun param(param: String, matcher: Param.() -> RequestMatcher) = matcher(Param(param))

    fun param(param: String) = Param(param)

    fun body(matcher: Body.() -> RequestMatcher) = matcher(Body)

    val body = Body

    sealed class MockMatherRule {
        object Path : MockMatherRule()
        data class Param(val name: String) : MockMatherRule()
        object Body : MockMatherRule() {
            inline fun <reified T> bodyMarch(noinline matcher: T.() -> Boolean) = BodyParamMather(matcher, BodyDataConverter(MockServerSettings.converterFactory!!, T::class.java))
            inline fun <reified T> bodyEq(src: String) = object : RequestMatcher {
                override fun invoke(path: String?, res: String?): Boolean {
                    return BodyDataConverter<T>(MockServerSettings.converterFactory!!, T::class.java).let {
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
