package io.github.infeez.kotlinmockserver

import com.google.gson.Gson
import io.github.rybalkinsd.kohttp.dsl.httpPost
import java.lang.RuntimeException

class TestApplication {

    private val gson: Gson = Gson()

    var host: String = ""
    var port: Int = 0

    private val users = mutableListOf(
        User("John", "123", ""),
        User("Peter", "456", ""),
        User("Joe", "798", "")
    )

    fun login(username: String, password: String): Result {
        val loginResult = httpPost {
            host = this@TestApplication.host
            port = this@TestApplication.port
            path = "/url/login"
            body {
                json {
                    "username" to username
                    "password" to password
                }
            }
        }

        return if (loginResult.isSuccessful) {
            val body = loginResult.body?.string()
            if (body.isNullOrEmpty()) {
                Result.Error(RuntimeException("Body empty"))
            } else {
                checkUserCredentials(body, username, password)
            }
        } else {
            if (loginResult.code == HTTP_NOT_FOUND) {
                Result.Error(RuntimeException("User not found"))
            } else {
                Result.Error(RuntimeException("Login error. Code: " + loginResult.code))
            }
        }
    }

    private fun checkUserCredentials(body: String, username: String, password: String): Result {
        val user = gson.fromJson(body, User::class.java)
        return if (users.find { it.username == user.username }?.username == username) {
            if (users.find { it.username == user.username }?.password == password) {
                Result.Success(user)
            } else {
                Result.Error(RuntimeException("Incorrect password"))
            }
        } else {
            Result.Error(RuntimeException("User not found"))
        }
    }

    companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
