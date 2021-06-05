package com.mock.matcher.impl

import com.mock.converter.BodyConverter
import com.mock.matcher.RequestMatcher

class BodyParamMather<T>(
    private val matcher: T.() -> Boolean,
    private val bodyConverter: BodyConverter<T>
) : RequestMatcher {

    override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
        return matcher(bodyConverter.convert(body!!))
    }
}