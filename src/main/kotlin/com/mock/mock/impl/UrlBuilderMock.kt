package com.mock.mock.impl

import com.mock.matcher.UrlBuilderMatcher
import com.mock.mock.Mock
import com.mock.util.RequestMethod

class UrlBuilderMock(
    private val urlBuilderMatcher: UrlBuilderMatcher,
    requestMethod: RequestMethod
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String?, body: String?, headers: Map<String, String>): Boolean {
        return super.isCoincided(path, method, body, headers) && urlBuilderMatcher.isCoincided()
    }
}