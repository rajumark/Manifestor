package com.manifestor.desktop

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform