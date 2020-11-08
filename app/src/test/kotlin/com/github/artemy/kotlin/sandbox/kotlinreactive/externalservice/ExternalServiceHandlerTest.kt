package com.github.artemy.kotlin.sandbox.kotlinreactive.externalservice

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus.OK
import org.springframework.web.reactive.function.server.ServerRequest

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class ExternalServiceHandlerTest {

    @Mock
    lateinit var externalServiceService: ExternalServiceService

    @Mock
    lateinit var serverRequest: ServerRequest

    @InjectMocks
    lateinit var externalServiceHandler: ExternalServiceHandler

    @Test
    fun coordsToPlaceHappyFlow() = runBlockingTest {
        `when`(externalServiceService.externalCall()).thenReturn("apricot")

        val response = externalServiceHandler.retrieveItems(serverRequest)

        assertEquals(OK, response.statusCode())
    }
}
