package com.infeez.mock

import com.infeez.mock.converter.ConverterFactory

object MockServerSettings {

    var converterFactory: ConverterFactory? = null
        get() {
            if (field == null) {
                error("ConverterFactory may not be null")
            }
            return field
        }
}
