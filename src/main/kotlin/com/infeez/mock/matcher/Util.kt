package com.infeez.mock.matcher

import com.infeez.mock.MockServerSettings
import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.extensions.extractQueryParams
import java.lang.reflect.Type
import java.util.regex.Pattern

typealias RequestMatcher = (path: String?, body: String?) -> Boolean

class PathMatcher(private val pattern: Pattern) : RequestMatcher {
    override fun invoke(path: String?, body: String?): Boolean {
        return path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.split("?")?.first()?.let {
            pattern.matcher(it).matches()
        } == true
    }
}

class QueryParamMatcher(private val param: String, private val pattern: Pattern) : RequestMatcher {
    override fun invoke(path: String?, body: String?): Boolean {
        return path.takeUnless { it.isNullOrEmpty() }?.decodeUrl()?.extractQueryParams()?.get(param)?.let {
            pattern.matcher(it).matches()
        } == true
    }
}

class BodyParamMather<T>(
    private val matcher: T.() -> Boolean,
    private val bodyConverter: BodyConverter<T>
) : RequestMatcher {
    override fun invoke(path: String?, body: String?): Boolean {
        return matcher(bodyConverter.convert(body!!))
    }
}

sealed class BodyConverter<T> {
    abstract fun convert(src: String): T

    object BodyString : BodyConverter<String>() {
        override fun convert(src: String): String {
            return src
        }
    }

    class BodyDataConverter<T>(private val type: Type) : BodyConverter<T>() {
        override fun convert(src: String): T {
            return MockServerSettings.converterFactory!!.from(src, type)
        }
    }
}

infix fun RequestMatcher.or(target: RequestMatcher): RequestMatcher = { p, b -> invoke(p, b) || target.invoke(p, b) }
infix fun RequestMatcher.and(target: RequestMatcher): RequestMatcher = { p, b -> invoke(p, b) && target.invoke(p, b) }
