package io.github.infeez.kotlinmockserver.mock

import io.github.infeez.kotlinmockserver.converter.ConverterFactory
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse

object MockConfiguration {

    private const val HTTP_NOT_FOUND_CODE = 404

    /**
     * Param used to parse a string into a model when matching the body of a request.
     * Use the same method as in your project.
     */
    var converterFactory: ConverterFactory? = null
        get() {
            if (field == null) {
                error("converterFactory may not be null!")
            }
            return field
        }

    /**
     * Default response if no mock is found when the client request for it. You can set any default response.
     * By default code is 404.
     */
    var defaultResponse: MockWebResponse = MockWebResponse(HTTP_NOT_FOUND_CODE)
}
