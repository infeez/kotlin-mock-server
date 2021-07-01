package io.github.infeez.kotlinmockserver.mock.impl

import io.github.infeez.kotlinmockserver.matcher.UrlBuilderMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.util.RequestMethod

class UrlBuilderMock(
    private val urlBuilderMatcher: UrlBuilderMatcher,
    requestMethod: RequestMethod
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String, body: String?, headers: Map<String, String>): Boolean {
        return super.isCoincided(path, method, body, headers) && urlBuilderMatcher.isCoincided()
    }

    override fun copy(): Mock {
        TODO("Not yet implemented")
    }
}
