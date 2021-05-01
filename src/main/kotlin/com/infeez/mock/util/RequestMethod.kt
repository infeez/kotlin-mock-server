package com.infeez.mock.util

sealed class RequestMethod(val method: String) {
    object ANY : RequestMethod("ANY")
    object GET : RequestMethod("GET")
    object HEAD : RequestMethod("HEAD")
    object POST : RequestMethod("POST")
    object PUT : RequestMethod("PUT")
    object DELETE : RequestMethod("DELETE")
    object CONNECT : RequestMethod("CONNECT")
    object OPTIONS : RequestMethod("OPTIONS")
    object TRACE : RequestMethod("TRACE")
    object PATCH : RequestMethod("PATCH")
}
