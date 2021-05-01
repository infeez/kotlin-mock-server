package com.infeez.mock.mock.impl

import com.infeez.mock.extensions.checkUrlParamWithAsterisk
import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.extensions.extractQueryParams
import com.infeez.mock.mock.Mock
import com.infeez.mock.util.RequestMethod

class UrlMock(
    private val requestMethod: RequestMethod,
    private val mockUrlSrc: String
) : Mock(requestMethod) {

    private val mockUrl: String = mockUrlSrc.split("?").first().let { u -> u.takeUnless { it.startsWith("/") }?.let { "/$it" } ?: u }
    private val queryParams: Map<String, String> = mockUrlSrc.extractQueryParams()

    override fun isCoincided(path: String, method: String?, body: String?): Boolean {
        val isCoincided = super.isCoincided(path, method, body)
        if (!isCoincided) {
            return false
        }

        val decodedUrl = path.decodeUrl()
        val decodedUrlSplited = decodedUrl.split("?")

        val isPassedByQuery = if (decodedUrlSplited.size == 2) {
            decodedUrl.extractQueryParams() == queryParams
        } else {
            true
        }

        val isUrlPassed = if (mockUrl.contains("*")) {
            mockUrl.checkUrlParamWithAsterisk(decodedUrl)
        } else {
            if (decodedUrlSplited.size == 2) {
                mockUrl == decodedUrlSplited[0]
            } else {
                mockUrl == decodedUrl
            }
        }

        return isUrlPassed && isPassedByQuery
    }

    override fun copy(): Mock {
        return UrlMock(requestMethod, mockUrlSrc).apply {
            mockWebResponse = this@UrlMock.mockWebResponse.copy()
        }
    }
}
