package com.infeez.mock.extensions

import java.net.URLDecoder
import java.net.URLEncoder

fun String.extractQueryParams(): Map<String, String> = split("?").takeIf {
    it.size == 2 && it.all { p -> p.isNotEmpty() }
}?.last()?.split("&")?.mapNotNull {
    it.split("=").takeIf { param ->
        param.size == 2 && param.all { p -> p.isNotEmpty() }
    }?.let { param ->
        param.first().decodeUrl() to param.last().decodeUrl()
    }
}?.toMap() ?: emptyMap()

fun String.decodeUrl(encoding: String = "utf-8"): String {
    return URLDecoder.decode(this, encoding)
}

fun String.encodeUrl(encoding: String = "utf-8"): String {
    return URLEncoder.encode(this, encoding)
}
