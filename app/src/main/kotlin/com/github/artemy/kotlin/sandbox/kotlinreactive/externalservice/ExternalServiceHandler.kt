package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import com.github.artemy.kotlin.sandbox.kotlinreactive.badGateway
import com.github.artemy.kotlin.sandbox.kotlinreactive.ifDebug
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ExternalServiceHandler(private val externalServiceService: ExternalServiceService) {
    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    suspend fun retrieveItems(serverRequest: ServerRequest): ServerResponse {

        val item = externalServiceService.externalCall()
            ?: return badGateway().bodyValueAndAwait("Unable to retrieve item")

        return ok().bodyValueAndAwait(item)
            .also { log.ifDebug("Retrieved item {}", item) }
    }
}
