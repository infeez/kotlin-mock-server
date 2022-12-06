import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.util.RequestMethod

fun mock(
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {
        emptyBody()
    }
): Lazy<Mock> {
    return MockContext().mockLazy(
        url = url,
        mockBuilder = mockBuilder
    )
}

fun mock(
    requestMethod: RequestMethod,
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {
        emptyBody()
    }
): Lazy<Mock> {
    return MockContext().mockLazy(
        requestMethod = requestMethod,
        url = url,
        mockBuilder = mockBuilder
    )
}

fun mock(
    matcher: RequestMatcher,
    mockBuilder: MockResponseContext.() -> Unit = {
        emptyBody()
    }
): Lazy<Mock> {
    return MockContext().mockLazy(
        matcher = matcher,
        mockBuilder = mockBuilder
    )
}

fun mock(
    requestMethod: RequestMethod,
    matcher: RequestMatcher,
    mockBuilder: MockResponseContext.() -> Unit = {
        emptyBody()
    }
): Lazy<Mock> {
    return MockContext().mockLazy(
        requestMethod = requestMethod,
        matcher = matcher,
        mockBuilder = mockBuilder
    )
}

fun listMocksOf(vararg mocks: Lazy<Mock>): Lazy<List<Mock>> {
    return lazy {
        mocks.map { mock ->
            val m by mock
            m
        }
    }
}
