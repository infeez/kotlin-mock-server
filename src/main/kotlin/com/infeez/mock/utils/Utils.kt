package com.infeez.mock.utils

import java.net.URLDecoder

fun extractQueryParams(url: String): Map<String, String> = url.split("?").takeIf {
    it.size == 2 && it.all { p -> p.isNotEmpty() }
}?.last()?.split("&")?.mapNotNull {
    it.split("=").takeIf { param ->
        param.size == 2 && param.all { p -> p.isNotEmpty() }
    }?.let { param ->
        URLDecoder.decode(param.first(), "utf-8") to URLDecoder.decode(param.last(), "utf-8")
    }
}?.toMap() ?: emptyMap()
