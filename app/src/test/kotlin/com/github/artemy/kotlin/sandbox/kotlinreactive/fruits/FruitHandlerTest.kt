package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitRequest
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitStatistics
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.util.CollectionUtils.toMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.time.Instant
import java.util.stream.Stream

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class FruitHandlerTest {

    private var fruitService: FruitService = mock()

    private val serverRequest: ServerRequest = mock()

    @InjectMocks
    lateinit var fruitHandler: FruitHandler

    @MethodSource
    @ParameterizedTest
    fun countFruitsHappyFlow(queryParams: MultiValueMap<String, String>) =
        runBlockingTest {
            whenever(serverRequest.queryParams()).thenReturn(queryParams)
            lenient().`when`(fruitService.getStatisticsForFruit(anyString())).thenReturn(FruitStatistics(listOf()))
            lenient().`when`(fruitService.getStatisticsOverall()).thenReturn(FruitStatistics(listOf()))

            val response = fruitHandler.getFruitsStatistics(serverRequest)

            assertEquals(OK, response.statusCode())
        }

    @Test
    fun saveFruitHappyFlow() = runBlockingTest {
        `when`(serverRequest.bodyToMono(FruitRequest::class.java)).thenReturn(Mono.just(FruitRequest("peach")))
        `when`(fruitService.saveFruit(any())).thenReturn(Mono.just(Fruit(42, "peach", Timestamp.from(Instant.now()))))
        val response = fruitHandler.saveFruit(serverRequest)

        assertEquals(OK, response.statusCode())
    }

    @Test
    fun saveFruitBadRequest() = runBlockingTest {
        `when`(serverRequest.bodyToMono(FruitRequest::class.java)).thenReturn(Mono.empty())
        val response = fruitHandler.saveFruit(serverRequest)

        assertEquals(BAD_REQUEST, response.statusCode())
    }

    @Test
    fun saveFruitDatabaseError() = runBlockingTest {
        `when`(serverRequest.bodyToMono(FruitRequest::class.java)).thenReturn(Mono.just(FruitRequest("coconut")))
        `when`(fruitService.saveFruit(any())).thenReturn(Mono.empty())

        val response = fruitHandler.saveFruit(serverRequest)

        assertEquals(INTERNAL_SERVER_ERROR, response.statusCode())
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun countFruitsHappyFlow(): Stream<Arguments> = Stream.of(
            Arguments.of(toMultiValueMap(mapOf<String, List<String>>())),
            Arguments.of(toMultiValueMap(mapOf("name" to listOf("apple"))))
        )
    }
}
