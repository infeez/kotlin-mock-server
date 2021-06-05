package com.mock.converter

import java.lang.reflect.Type

sealed class BodyConverter<T> {
    abstract fun convert(src: String): T

    object BodyString : BodyConverter<String>() {
        override fun convert(src: String): String {
            return src
        }
    }

    class BodyDataConverter<T>(
        private val converterFactory: ConverterFactory,
        private val type: Type
    ) : BodyConverter<T>() {
        override fun convert(src: String): T {
            return converterFactory.from(src, type)
        }
    }
}