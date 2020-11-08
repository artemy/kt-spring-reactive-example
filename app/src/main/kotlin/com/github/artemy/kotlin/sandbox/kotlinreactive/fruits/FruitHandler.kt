package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitRequest
import com.github.artemy.kotlin.sandbox.kotlinreactive.ifDebug
import com.github.artemy.kotlin.sandbox.kotlinreactive.internalServerError
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class FruitHandler(private val fruitService: FruitService) {

    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    suspend fun saveFruit(serverRequest: ServerRequest): ServerResponse {
        val fruitRequest = serverRequest.bodyToMono(FruitRequest::class.java).awaitFirstOrNull()
            ?: return badRequest().buildAndAwait()
                .also { log.error("Bad request: could not decode fruit save request") }

        fruitService.saveFruit(Fruit(fruitRequest.name)).awaitFirstOrNull()
            ?: return internalServerError().buildAndAwait()
                .also { log.error("Server error: could not save fruit") }

        return ok().buildAndAwait()
            .also { log.ifDebug("Stored {}", fruitRequest) }
    }

    suspend fun getFruitsStatistics(serverRequest: ServerRequest): ServerResponse {
        val fruitName = serverRequest.queryParamOrNull("name")
            ?: return ok().bodyValueAndAwait(fruitService.getStatisticsOverall())
                .also { log.ifDebug("Returning overall statistics") }

        return ok().bodyValueAndAwait(fruitService.getStatisticsForFruit(fruitName))
            .also { log.ifDebug("Returning overall statistics for fruit {}", fruitName) }
    }
}
