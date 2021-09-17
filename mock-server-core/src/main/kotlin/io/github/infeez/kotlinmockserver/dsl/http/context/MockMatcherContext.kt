package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.rule.MockMatherRule.Body
import io.github.infeez.kotlinmockserver.rule.MockMatherRule.Header
import io.github.infeez.kotlinmockserver.rule.MockMatherRule.Path
import io.github.infeez.kotlinmockserver.rule.MockMatherRule.Query

/**
 * This class realise DSL-context to build url matcher.
 */
class MockMatcherContext {

    /**
     * Matcher for header request. With [Header] DSL-context.
     */
    fun header(name: String, matcher: Header.() -> RequestMatcher) = matcher(Header(name))

    /**
     * Matcher for header request.
     */
    fun header(name: String) = Header(name)

    /**
     * Matcher for url path. With [Path] DSL-context.
     */
    fun path(matcher: Path.() -> RequestMatcher) = matcher(Path)

    /**
     * Matcher for url path.
     */
    val path = Path

    /**
     * Matcher for query in url. With [Query] DSL-context.
     */
    fun query(param: String, matcher: Query.() -> RequestMatcher) = matcher(Query(param))

    /**
     * Matcher for query in url.
     */
    fun query(param: String) = Query(param)

    /**
     * Matcher for body in request. With [Body] DSL-context.
     */
    fun body(matcher: Body.() -> RequestMatcher) = matcher(Body)

    /**
     * Matcher for body in request.
     */
    val body = Body
}
