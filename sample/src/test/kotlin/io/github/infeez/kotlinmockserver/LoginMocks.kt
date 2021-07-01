package io.github.infeez.kotlinmockserver

import io.github.infeez.kotlinmockserver.dsl.http.mock

object LoginMocks {

    val loginByUserJohn = mock {
        path { eq("/url/login") }
    } on {
        body("""{ "username" : "John", "password": "123", "fullName" : "John Doe" }""")
    }
}
