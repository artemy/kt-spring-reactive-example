package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitRequest
import com.github.artemy.kotlin.sandbox.kotlinreactive.ifDebug
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
    fun saveFruit(@RequestBody fruitRequest: FruitRequest) =
        fruitService.saveFruit(Fruit(fruitRequest.name))
            .map { ok().build<String>() }
            .doOnSuccess { log.ifDebug("Stored {}", fruitRequest) }
            .onErrorReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR).build())

    @GetMapping
    fun getFruitsStatisticsOverall() =
        fruitService.getStatisticsOverall()
            .map { ok().body(it) }
            .doOnSuccess { log.ifDebug("Returning overall statistics") }

    @GetMapping(params = ["name"])
    fun getFruitsStatistics(@RequestParam("name") fruitName: String) =
        fruitService.getStatisticsForFruit(fruitName)
            .map { ok().body(it) }
            .doOnSuccess { log.ifDebug("Returning overall statistics for fruit {}", fruitName) }
}
