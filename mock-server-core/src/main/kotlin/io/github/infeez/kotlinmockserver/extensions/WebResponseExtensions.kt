package io.github.infeez.kotlinmockserver.extensions

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import java.lang.reflect.Type

inline fun <T> MockWebResponse.copyResponse(type: Type, change: T.() -> Unit): MockWebResponse {
    return copy(body = io.github.infeez.kotlinmockserver.MockServerConfiguration.converterFactory!!.let { it.to(it.from<T>(body ?: error("Body may not be null!"), type).apply(change)) })
}

inline fun <reified T> MockWebResponse.copyResponse(change: T.() -> Unit): MockWebResponse {
    return copyResponse(T::class.java, change)
}
