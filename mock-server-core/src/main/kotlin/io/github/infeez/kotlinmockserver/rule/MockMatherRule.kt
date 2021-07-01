package io.github.infeez.kotlinmockserver.rule

import io.github.infeez.kotlinmockserver.converter.BodyConverter.BodyDataConverter
import io.github.infeez.kotlinmockserver.converter.BodyConverter.BodyString
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.BodyParamMather
import io.github.infeez.kotlinmockserver.matcher.impl.HeaderParamMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.PathMatcher
import io.github.infeez.kotlinmockserver.matcher.impl.QueryParamMatcher
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import java.util.regex.Pattern

/**
 * The class describes methods for combining rule mocks.
 *
 */
sealed class MockMatherRule {

    /**
     * Rule for combining http header params.
     *
     * @param name - [String] a name of header key.
     */
    data class Header(val name: String) : MockMatherRule()

    /**
     * Rule for combining http path.
     *
     */
    object Path : MockMatherRule()

    /**
     * Rule for combining http query params in path.
     *
     * @param name - [String] a name of query key. Example: path.com?[name]=value
     */
    data class Query(val name: String) : MockMatherRule()

    /**
     * Rule for combining http request body params.
     *
     */
    object Body : MockMatherRule() {
        @PublishedApi
        internal val mockConfiguration: MockConfiguration = MockConfiguration

        /**
         * Match if request body is null or empty.
         */
        fun isNullOrEmpty() = object : RequestMatcher {
            override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
                return body.isNullOrEmpty()
            }
        }

        /**
         * Mather for match body field by model [T]. Provides a model [T] context for field comparison.
         * The code in context should return a [Boolean].
         *
         * Example:   bodyMarch<YourBodyModel> {
         name1 == "value1" && name2 == 2
         }
         *
         * @param matcher - DSL-context for build [Boolean] condition with model fields.
         */
        inline fun <reified T> bodyMarch(noinline matcher: T.() -> Boolean): RequestMatcher {
            return BodyParamMather(matcher, BodyDataConverter(mockConfiguration.converterFactory!!, T::class.java))
        }

        /**
         * The matcher converts [src] and the request body into a model and compares field values.
         *
         * @param src - [String] a string for convert into a model [T]
         */
        inline fun <reified T> bodyEq(src: String) = object : RequestMatcher {
            override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
                return BodyDataConverter<T>(mockConfiguration.converterFactory!!, T::class.java).let {
                    it.convert(src) == it.convert(body!!)
                }
            }
        }
    }

    /**
     * A matcher to any values. Always return true.
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
