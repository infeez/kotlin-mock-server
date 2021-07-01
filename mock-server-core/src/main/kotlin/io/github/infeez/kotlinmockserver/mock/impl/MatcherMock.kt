package io.github.infeez.kotlinmockserver.mock.impl

import io.github.infeez.kotlinmockserver.extensions.decodeUrl
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.util.RequestMethod

class MatcherMock(
    private val requestMethod: RequestMethod,
    private val requestMatcher: RequestMatcher
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String, body: String?, headers: Map<String, String>): Boolean {
        val isCoincided = super.isCoincided(path, method, body, headers)
        if (!isCoincided) {
            return false
        }

        return requestMatcher.invoke(path.decodeUrl(), body, headers)
    }

    override fun copy(): Mock {
        return MatcherMock(requestMethod, requestMatcher).apply {
            mockWebResponse = this@MatcherMock.mockWebResponse.copy()
        }
    }
}
