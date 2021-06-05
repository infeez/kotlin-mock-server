## About The Project

The library for a simple mocking HTTP server using a URL condition of any complexity.

Here's why:
* For quick application testing
* Easy to understand without deep knowledge of Kotlin
* Kotlin-DSL for mocking

Main features:
* Starting a server for mocking http requests
* Creating mock parameters using dsl context
* Ability to combine parameters using ```and \ or``` constructs for a more flexible condition
* Mock condition created by path, query, body, combining in any way
* Mocking using direct url specification
* Mock response parameters: response delay, response body, headers, response code
* Ability to create your own mock server (netty(experimental) and okhttp are available)
* Ability to replace, remove, add mock during test running
* Ability to replace mock response parameters during test running
* Rule support for the server. The server managed by ```Rule```.
## Getting Started
### Installation
TBD!

## Usage
### Ways for create server and usage 
Using rule for create and start server in your test class. Using OkHttp as a sample:
```kotlin
    @get:Rule
    val mockServer = okHttpMockServer {}
```
you can define mock inside as many times as you like ```okHttpMockServer```: 
```kotlin
    @get:Rule
    val mockServer = okHttpMockServer {
        mock("/rule/okhttp/test") {
            body("its ok")
        }
        mock("/rule/okhttp/test") {
            body("its ok")
        }
    }
```
In other place you can use ```mockServer``` with ```mock``` or ```mocks``` functions.
```kotlin
mockServer.mock("url") {}

mockServer.mocks {
    mock("url1") {}
    mock("url2") {}
}
```
For create custom server you need to inheritance Server abstract class. TBD
### MockServer configuration
The Mock server has two configurations.
</br>The first set when the server starts and contains network information.
</br>The second singleton configuration used to configure the mocks.
</br>First ```Configuration``` sets when server created:
```kotlin
    @get:Rule
    val mockServer = okHttpMockServer(Configuration.custom {
        host = "localhost"
        port = 8888
    })
```
```Configuration.custom``` block have ```host``` and ```port```. Sets the host and port on which the server will run. Make sure the port is not bind! 
</br>By default server used ```Configuration.default()```. In it the host is the ```localhost```, and the port is any unbinded from ```50013``` to ```65535```.
</br>
</br> Second configuration singleton ```MockServerConfiguration``` contains ```converterFactory``` and ```defaultResponse```.
```kotlin
    @get:Rule
    val mockServer = okHttpMockServer(Configuration.custom {
        host = "localhost"
        port = 8888
    }, {
        converterFactory = object : ConverterFactory {
            override fun <T> from(value: String, type: Type): T {
                TODO("Not yet implemented")
            }

            override fun <T> to(value: T): String {
                TODO("Not yet implemented")
            }
        }
        defaultResponse = MockWebResponse(404, body = "Mock not found!")
    })
```
```converterFactory``` - needed to parse a string into a model when matching the body of a request. Use the same method as in your project. Gson example:
```kotlin
    private val gsonConverterFactory = object : ConverterFactory {
        private val gson = Gson()
        override fun <T> from(value: String, type: Type): T {
            return gson.fromJson(value, type)
        }

        override fun <T> to(value: T): String {
            return gson.toJson(value)
        }
    }
```
```defaultResponse``` - the default response if no mock is found when the client request for it. You can set any default response. By default ```MockWebResponse(404)```.
### Direct mock
Direct mock is simple way to define condition with URL. Mock server will be equal url as it is with url in your application client.
```kotlin
val mock = mock("this/called/in/your/client") {
    body("response body")
}
```
That's all about direct url.
### Matcher mock
Matcher mock a way to create combination by path, header, query, body:
#### Path matcher
Sample for path equal ```/mock/url``` using ```eq``` matcher
```kotlin
val mock = mock {
    path { eq("/mock/url") }
} on { 
    body("response body")
}
```
#### Header matcher
Sample for header ```Content-Type: text/html; charset=utf-8``` using ```eq``` matcher
```kotlin
val mock = mock {
    header("Content-Type") { eq("text/html; charset=utf-8") }
} on { 
    body("response body")
}
```
#### Query matcher
Sample for query ```/mock/url?param1=somevalue``` using ```eq``` matcher
```kotlin
val mock = mock {
    query("param1") { eq("somevalue") }
} on { 
    body("response body")
}
```
#### Body matcher
Sample for body as json ```{"name1":"value1","name2":2}``` using ```eq``` matcher
```kotlin
val mock = mock {
    body { eq("""{"name1":"value1","name2":2}""") }
} on { 
    body("response body")
}
```
Remember the eq matcher compares strings as is! If you need to compare the body, use the ```bodyEq``` matcher. This matcher converts the string into a model and compares the fields.
</br> Body will be converted using ConverterFactory. See more in the Configuration chapter.
```kotlin
val mock = mock {
    body { bodyEq<YourBodyModel>("""{ "name1" : "value1" , "name2" : 2}""") }
} on { 
    body("response body")
}
```
If you need to compare each field in the body individually, use ```bodyMarch``` matcher. Json in body request:
```json
{ 
    "name1" : "value1" , 
    "name2" : 2
}
```
and mock for this body with ```bodyMarch```:
```kotlin
val mock = mock {
    body { 
        bodyMarch<YourBodyModel> {
            name1 == "value1" && name2 == 2
        }
    }
} on { 
    body("response body")
}
```
of course the YourBodyModel must be defined in your project for a proper comparison:
```kotlin
data class YourBodyModel(val name1: String, val name2: Int)
```
#### Other matchers
You can also use any of the matchers: ```any```,```eq```, ```startWith```,```endsWith```,```matches``` and create any combination. Like this:
```kotlin
val mock = mock {
    path { startWith("/mock") }
} on { 
    body("response body")
}
```
```any``` - triggers on any url
</br>```eq``` - triggers when url in mock and url in client are equal
</br>```startWith``` - triggers when client url starts with value in mock
</br>```endsWith``` - triggers when client url ends with value in mock
</br>```matches``` - triggers when url in client passed by regex value in mock url
</br></br>
To combine matchers, you have to use: ```and```,```or```. Like this:
```kotlin
val mock = mock {
    path { startWith("/mock") } and path { endWith("url") } or path { eq("/mock/url") }
} on { 
    body("response body")
}
```
and combine other request parameters:
```kotlin
val mock = mock {
    path { 
        startWith("/mock/url") 
    } and header("Content-Type") { 
        eq("text/html; charset=utf-8") 
    } and query("param1") { 
        endsWith("lue") 
    } and body {
        bodyMarch<YourBodyModel> {
            name1 == "value1" && name2 == 2
        }
    }
} on { 
    body("response body")
}
```
### Mock response parameters
The mock response may contain: HTTP-code, headers, body, delay.
```kotlin
val mock = mock { path { eq("/mock/url") } } on {
    code(200)
    headers {
        "name" to "value"
    }
    delay(1L, TimeUnit.SECONDS)
    body("response body")
    emptyBody()
}
```
In ```code(200)``` you can specify any HTTP-code.
</br>Block ```headers``` takes key/value pair used infix fun ```to```. You can also add the header using vararg method ```headers("name1" to "value1", "name2" to "value2")```
The answer can be delayed by the method ```delay(1000L)``` without the second argument because it's optional. The default is milliseconds. 
But you can use this method with two arguments and set the desired TimeUnit ```delay(1L, TimeUnit.MINUTES)```.
</br> ```body(String|InputStream|File)``` method sets the body of the answer. You can use body overloading by specifying ```String```, ```InputStream``` or ```File```.
</br> ```emptyBody()``` just sets body is empty.
### Add mock to mock server
Remember! After you create a mock, you must add it to the mock server list!
```kotlin
val mock1 = mock { path { eq("/url1") } } on { body("response body1") }
val mock2 = mock { path { eq("/url2") } } on { body("response body2") }
val mock3 = mock { path { eq("/url3") } } on { body("response body3") }

mockServer.add(mock1)
mockServer.add(mock2)
mockServer.add(mock3)

// or

mockServer.addAll(mock1, mock2, mock3)
```
This may be necessary for different application scenarios. For example:
```kotlin
val login = mock { path { eq("/login") } } on { body("""{ "login":"userLogin", "password":"userPassword" }""") }
val getUserInfo = mock { path { eq("/userInfo") } } on { body("""{ "userName":"userName", "userSurname":"userSurname" }""") }
val userLoginTestScenario = listOf(login, getUserInfo)

mockServer.addAll(userLoginTestScenario)
```
or like this:
```kotlin
val userLoginTestScenario = mocks {
    mock { path { eq("/login") } } on { body("""{ "login":"userLogin", "password":"userPassword" }""") }
    mock { path { eq("/userInfo") } } on { body("""{ "id":1, "userName":"userName", "userSurname":"userSurname" }""") }
}

mockServer.addAll(userLoginTestScenario)
```

## License
Distributed under the Apache License 2.0. See `LICENSE` for more information.
## Contact
Vadim Vasyanin - infeez@gmail.com
