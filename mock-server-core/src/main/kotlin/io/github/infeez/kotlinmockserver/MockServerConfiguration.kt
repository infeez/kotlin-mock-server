package io.github.infeez.kotlinmockserver

import io.github.infeez.kotlinmockserver.converter.ConverterFactory
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse

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
