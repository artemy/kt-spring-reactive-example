package com.github.artemy.kotlin.sandbox.kotlinreactive

import org.slf4j.Logger

fun Logger.ifDebug(msg: String, vararg arguments: Any) {
    if (this.isDebugEnabled) {
        this.debug(msg, arguments)
    }
}