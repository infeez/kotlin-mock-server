package com.mock.converter

import java.lang.reflect.Type

/**
 * MockServer does not need to know about the serialization implementation in your project.
 * You need to implement this interface in your project using your serialization.
 *
 * Please check documentation or simple project.
 *
 */
interface ConverterFactory {
    /**
     * This method is needed to convert [String] to [T]
     *
     * @param value - [String] value to convert.
     * @param type  - The [Type] of your class
     *
     * @return [T] convert result.
     */
    fun <T> from(value: String, type: Type): T

    /**
     * This method is needed to convert [T] to [String]
     *
     * @param value - [T] value to convert.
     *
     * @return [String] convert result.
     */
    fun <T> to(value: T): String
}
