package com.infeez.mock.mock.impl

import com.infeez.mock.matcher.UrlBuilder
import com.infeez.mock.mock.Mock
import com.infeez.mock.util.RequestMethod

class UrlBuilderMock(
    private val urlBuilder: UrlBuilder,
    requestMethod: RequestMethod
) : Mock(requestMethod) {

    override fun isCoincided(path: String, method: String?, body: String?): Boolean {
        return super.isCoincided(path, method, body) && urlBuilder.isCoincided()
    }
}
