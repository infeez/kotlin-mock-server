package io.github.infeez.kotlinmockserver

sealed class Result {
    class Success<T>(val data: T) : Result()
    class Error(val exception: RuntimeException) : Result()
}
