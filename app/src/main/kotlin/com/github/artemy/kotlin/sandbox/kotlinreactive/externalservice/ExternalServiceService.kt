package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.ExternalServiceResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class ExternalServiceService(private val webClient: WebClient) {
    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    fun externalCall(): Mono<String> = webClient.get().uri { it.path("/api").build() }
        .accept(APPLICATION_JSON)
        .retrieve()
        .bodyToMono<ExternalServiceResponse>()
        .map { it.items.first() }
        .doOnError { log.error("External backend error occurred", it) }
}
