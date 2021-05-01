package com.infeez.mock.extensions

import com.infeez.mock.MockServerSettings
import com.infeez.mock.mockmodel.MockWebResponse
import java.lang.reflect.Type

inline fun <T> MockWebResponse.copyResponse(type: Type, change: T.() -> Unit): MockWebResponse {
    return copy(body = MockServerSettings.converterFactory!!.let { it.to(it.from<T>(body ?: error("Body may not be null!"), type).apply(change)) })
}

inline fun <reified T> MockWebResponse.copyResponse(change: T.() -> Unit): MockWebResponse {
    return copyResponse(T::class.java, change)
}
