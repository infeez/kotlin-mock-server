package io.github.infeez.kotlinmockserver.matcher.impl

import io.github.infeez.kotlinmockserver.converter.BodyConverter
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher

class BodyParamMather<T>(
    private val matcher: T.() -> Boolean,
    private val bodyConverter: BodyConverter<T>
) : RequestMatcher {

    override fun invoke(path: String?, body: String?, headers: Map<String, String>): Boolean {
        return matcher(bodyConverter.convert(body!!))
    }
}