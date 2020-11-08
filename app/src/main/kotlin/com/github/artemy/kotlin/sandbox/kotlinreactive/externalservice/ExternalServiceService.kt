package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.ExternalServiceResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow

@Service
class ExternalServiceService(private val webClient: WebClient) {
    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    suspend fun externalCall(): String? {
        return webClient.get().uri { it.path("/api").build() }
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToFlow<ExternalServiceResponse>()
            .map { it.items.firstOrNull() }
            .catch { emit(backendError(it)) }
            .singleOrNull()
    }

    private fun backendError(error: Throwable): String? {
        log.error("External backend error occurred", error)
        return null
    }
}
