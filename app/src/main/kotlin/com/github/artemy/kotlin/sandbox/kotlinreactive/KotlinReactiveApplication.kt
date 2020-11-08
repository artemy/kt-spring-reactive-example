package com.github.artemy.kotlin.sandbox.kotlinreactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(proxyBeanMethods = false)
class KotlinReactiveApplication

fun main(args: Array<String>) {
    runApplication<KotlinReactiveApplication>(*args)
}
