package com.infeez.mock

import java.util.concurrent.TimeUnit

@Deprecated(
    message = "Please use new API for mock. See more in documentation.",
    replaceWith = ReplaceWith("MockWebParams", "com.infeez.mock.util")
)
class MockResponseParameterDelayBuilder {
    var delay: Long = 0
    var unit: TimeUnit = TimeUnit.MILLISECONDS
}
