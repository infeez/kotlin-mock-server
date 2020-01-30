@file:Suppress("UNCHECKED_CAST")

package com.infeez.mock.converter

import java.lang.reflect.Type

interface ConverterFactory {
    fun <T> convert(value: String, type: Type): T
}

internal object DataConverter {

    internal var converterFactory: ConverterFactory? = null

    fun <T> convert(value: String, type: Type): T {
        if (converterFactory == null) {
            error("Converter may not be null")
        }

        return converterFactory!!.convert(value, type) as T
    }
}
