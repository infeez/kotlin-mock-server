package com.mock.mock.impl

import com.mock.extensions.decodeUrl
import com.mock.matcher.RequestMatcher
import com.mock.mock.Mock
import com.mock.util.RequestMethod

class MatcherMock(
    private val requestMethod: RequestMethod,
    private val requestMatcher: RequestMatcher
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String?, body: String?, headers: Map<String, String>): Boolean {
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