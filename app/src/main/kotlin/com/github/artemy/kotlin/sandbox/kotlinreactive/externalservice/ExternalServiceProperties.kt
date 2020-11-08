package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.backends.external")
data class ExternalServiceProperties(var url: String = "", var apiKey: String = "")
