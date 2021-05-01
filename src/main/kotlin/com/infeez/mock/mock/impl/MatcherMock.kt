package com.infeez.mock.mock.impl

import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.matcher.RequestMatcher
import com.infeez.mock.mock.Mock
import com.infeez.mock.util.RequestMethod

class MatcherMock(
    private val requestMethod: RequestMethod,
    private val requestMatcher: RequestMatcher
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String?, body: String?): Boolean {
        val isCoincided = super.isCoincided(path, method, body)
        if (!isCoincided) {
            return false
        }

        return requestMatcher.invoke(path.decodeUrl(), body)
    }

    override fun copy(): Mock {
        return MatcherMock(requestMethod, requestMatcher).apply {
            mockWebResponse = this@MatcherMock.mockWebResponse.copy()
        }
    }
}
