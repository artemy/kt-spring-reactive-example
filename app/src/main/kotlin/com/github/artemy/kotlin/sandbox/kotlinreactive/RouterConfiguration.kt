package com.github.artemy.kotlin.sandbox.kotlinreactive

import com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice.ExternalServiceHandler
import com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice.ExternalServiceProperties
import com.github.artemy.kotlin.sandbox.kotlinreactive.fruits.FruitHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration(proxyBeanMethods = false)
class RouterConfiguration {

    @Bean
    fun externalServiceWebClient(externalServiceProperties: ExternalServiceProperties) =
        WebClient.create(externalServiceProperties.url)

    @Bean
    fun routes(fruitHandler: FruitHandler, externalServiceHandler: ExternalServiceHandler) = coRouter {
        "/fruits".nest {
            (method(GET) and accept(APPLICATION_JSON)).invoke(fruitHandler::getFruitsStatistics)
            (method(POST) and contentType(APPLICATION_JSON)).invoke(fruitHandler::saveFruit)
        }
        "/external".nest {
            method(POST, externalServiceHandler::retrieveItems)
        }
    }
}

fun internalServerError() = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
fun badGateway() = ServerResponse.status(HttpStatus.BAD_GATEWAY)
