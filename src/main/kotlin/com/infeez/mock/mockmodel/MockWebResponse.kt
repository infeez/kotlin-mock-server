package com.infeez.mock.mockmodel

data class MockWebResponse(
    val code: Int,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val mockWebResponseParams: MockWebResponseParams = MockWebResponseParams()
) {
    data class MockWebResponseParams(
        val delay: Long = 0L
    )
}
