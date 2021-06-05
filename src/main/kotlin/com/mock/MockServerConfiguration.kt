package com.mock

import com.mock.converter.ConverterFactory
import com.mock.mockmodel.MockWebResponse

/**
 *
 *
 */
object MockServerConfiguration {

    var converterFactory: ConverterFactory? = null
        get() {
            if (field == null) {
                error("converterFactory may not be null!")
            }
            return field
        }

    var defaultResponse: MockWebResponse = MockWebResponse(404)
}