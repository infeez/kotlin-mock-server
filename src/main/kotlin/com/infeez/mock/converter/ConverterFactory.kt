package com.infeez.mock.converter

import java.lang.reflect.Type

interface ConverterFactory {
    fun <T> from(value: String, type: Type): T
    fun <T> to(value: T): String
}
