package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.converter.BodyConverter.BodyDataConverter
import io.github.infeez.kotlinmockserver.converter.BodyConverter.BodyString
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext.MockMatherRule.Body
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext.MockMatherRule.Header
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext.MockMatherRule.Path
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext.MockMatherRule.Query
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.BodyParamMather
import io.github.infeez.kotlinmockserver.matcher.impl.HeaderParamMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.PathMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.QueryParamMatcher
import java.util.regex.Pattern

/**
 * This class realise DSL-context to build url matcher.
 *
 */
class MockMatcherContext {

    /**
     * Matcher for header request. With [Header] DSL-context.
     *
     */
    fun header(name: String, matcher: Header.() -> RequestMatcher) = matcher(Header(name))

    /**
     * Matcher for header request.
     *
     */
    fun header(name: String) = Header(name)

    /**
     * Matcher for url path. With [Path] DSL-context.
     *
     */
    fun path(matcher: Path.() -> RequestMatcher) = matcher(Path)

    /**
     * Matcher for url path.
     *
     */
    val path = Path

    /**
     * Matcher for query in url. With [Query] DSL-context.
     *
     */
    fun query(param: String, matcher: Query.() -> RequestMatcher) = matcher(Query(param))

    /**
     * Matcher for query in url.
     *
     */
    fun query(param: String) = Query(param)

    /**
     * Matcher for body in request. With [Body] DSL-context.
     *
     */
    fun body(matcher: Body.() -> RequestMatcher) = matcher(Body)

    /**
     * Matcher for body in request.
     *
     */
    val body = Body

    sealed class MockMatherRule {
        data class Header(val name: String) : MockMatherRule()
        object Path : MockMatherRule()
        data class Query(val name: String) : MockMatherRule()
        object Body : MockMatherRule() {
            inline fun <reified T> bodyMarch(noinline matcher: T.() -> Boolean) = BodyParamMather(matcher, BodyDataConverter(io.github.infeez.kotlinmockserver.MockServerConfiguration.converterFactory!!, T::class.java))
            inline fun <reified T> bodyEq(src: String) = object : RequestMatcher {
                override fun invoke(path: String?, res: String?, headers: Map<String, String>): Boolean {
                    return BodyDataConverter<T>(io.github.infeez.kotlinmockserver.MockServerConfiguration.converterFactory!!, T::class.java).let {
                        it.convert(src) == it.convert(res!!)
                    }
                }
            }
        }

        /**
         * A matcher to any values. Always return true.
         *
         */
        fun any(): RequestMatcher = { _, _, _ -> true }

        /**
         * A matcher to accurately compare strings.
         *
         * @param value - [String] value for match.
         */
        fun eq(value: String) = matches(exact(value))

        /**
         * A matcher triggered if the string starts with the specified value.
         *
         * @param value - [String] value for match.
         */
        fun startWith(value: String) = matches(prefix(value))

        /**
         * A matcher triggered if the string ends with the specified value.
         *
         * @param value - [String] value for match.
         */
        fun endsWith(value: String) = matches(suffix(value))

        /**
         * A matcher takes any Regex pattern for match string.
         *
         * @param pattern [Pattern] a regex patter for match.
         */
        fun matches(pattern: Pattern) = when (this) {
            is Header -> HeaderParamMatcher(name, pattern)
            is Body -> BodyParamMather({ pattern.matcher(this).matches() }, BodyString)
            is Query -> QueryParamMatcher(name, pattern)
            is Path -> PathMatcher(pattern)
        }

        private fun exact(text: String) = Pattern.compile(Pattern.quote(text))
        private fun prefix(text: String) = Pattern.compile("^" + Pattern.quote(text) + ".*$")
        private fun suffix(text: String) = Pattern.compile("^.*" + Pattern.quote(text) + "$")
    }
}
