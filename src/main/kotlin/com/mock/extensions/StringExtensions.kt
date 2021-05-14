package com.mock.extensions

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

fun String.removeFirstAndLastSlashInUrl(): String {
    if (isEmpty()) {
        return this
    }

    var result = this

    if (result.first() == '/') {
        result = result.substring(1, result.length)
    }

    if (result.last() == '/') {
        result = result.substring(0, result.length - 1)
    }

    return result
}

fun String.checkUrlParamWithAsterisk(targetUrl: String): Boolean {
    val f = split("?")[0].removeFirstAndLastSlashInUrl().split("/").toMutableList()
    val s = targetUrl.split("?")[0].removeFirstAndLastSlashInUrl().split("/").toMutableList()
    while (f.count { it == "*" } > 0) {
        s.removeAt(f.indexOf("*"))
        f.remove("*")
    }

    return f == s
}
