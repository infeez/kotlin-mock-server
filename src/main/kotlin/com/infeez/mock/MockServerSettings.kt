package com.infeez.mock

import com.infeez.mock.converter.ConverterFactory
import com.infeez.mock.mockmodel.MockWebResponse

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

    var defaultResponse: MockWebResponse? = null
}
