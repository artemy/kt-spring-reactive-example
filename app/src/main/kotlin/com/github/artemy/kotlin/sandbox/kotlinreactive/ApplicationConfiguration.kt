package com.github.artemy.kotlin.sandbox.kotlinreactive

import com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice.ExternalServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class ApplicationConfiguration {

    @Bean
    fun externalServiceWebClient(externalServiceProperties: ExternalServiceProperties) =
        WebClient.create(externalServiceProperties.url)
}