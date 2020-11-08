package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitRequest
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitStatistics
import com.github.artemy.kotlin.sandbox.kotlinreactive.ifDebug
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fruits")
class FruitController(private val fruitService: FruitService) {

    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    @PostMapping
    suspend fun saveFruit(@RequestBody fruitRequest: FruitRequest): ResponseEntity<String> {
        fruitService.saveFruit(Fruit(fruitRequest.name)).awaitFirstOrNull()
            ?: return ResponseEntity.status(INTERNAL_SERVER_ERROR).build()

        return ok().build<String>().also { log.ifDebug("Stored {}", fruitRequest) }
    }

    @GetMapping
    suspend fun getFruitsStatisticsOverall(): ResponseEntity<FruitStatistics> {
        return ok().body(fruitService.getStatisticsOverall())
            .also { log.ifDebug("Returning overall statistics") }
    }

    @GetMapping(params = ["name"])
    suspend fun getFruitsStatistics(@RequestParam("name") fruitName: String): ResponseEntity<FruitStatistics> {
        return ok().body(fruitService.getStatisticsForFruit(fruitName))
            .also { log.ifDebug("Returning overall statistics for fruit {}", fruitName) }
    }
}
