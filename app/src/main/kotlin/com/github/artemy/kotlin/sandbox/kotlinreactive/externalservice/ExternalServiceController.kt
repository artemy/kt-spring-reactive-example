package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import com.github.artemy.kotlin.sandbox.kotlinreactive.ifDebug
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/external")
class ExternalServiceController(private val externalServiceService: ExternalServiceService) {
    private val log by lazy { LoggerFactory.getLogger(javaClass) }

    @PostMapping
    fun retrieveItems() = externalServiceService.externalCall()
        .map { ok().body(it) }
        .doOnSuccess { log.ifDebug("Retrieved item {}", it) }
        .onErrorReturn(ResponseEntity.status(BAD_GATEWAY).body("Unable to retrieve item"))
}
