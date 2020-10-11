package com.infeez.mock

import com.infeez.mock.converter.ConverterFactory

object MockServerSettings {

    var converterFactory: ConverterFactory? = null
        get() {
            if (field == null) {
                error("converterFactory may not be null!")
            }
            return field
        }

    var failSafeServerUrl: String = ""
        get() {
            if (field.isEmpty()) {
                error("failSafeServerUrl may not be empty!")
            }
            return field
        }
}
