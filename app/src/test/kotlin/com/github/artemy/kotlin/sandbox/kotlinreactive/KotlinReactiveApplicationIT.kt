package com.github.artemy.kotlin.sandbox.kotlinreactive

import com.github.artemy.kotlin.sandbox.kotlinreactive.KotlinReactiveApplicationIT.Companion.WIREMOCK_PORT
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitStatistics
import com.github.artemy.kotlin.sandbox.kotlinreactive.fruits.FruitRepository
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.expectBody
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier
import wiremock.org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import wiremock.org.apache.http.HttpStatus.SC_OK

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
@ActiveProfiles("integration")
@TestPropertySource(properties = ["wiremock.server.port:$WIREMOCK_PORT"])
class KotlinReactiveApplicationIT : AbstractDatabaseIT() {

    @Autowired
    lateinit var fruitRepository: FruitRepository

    @BeforeEach
    fun each() {
        cleanDatabase()
        wireMock.stubFor(
            get(urlPathEqualTo("/api"))
                .willReturn(
                    aResponse().withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(SC_OK)
                        .withBody(DEFAULT_EXTERNAL_SERVICE_RESPONSE)
                )
        )
    }

    @Test
    fun testGetFruitsStatisticsOverallEmptyDb() {
        webTestClient.get().uri("/fruits")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.fruitsPerDay").isEmpty
    }

    @Test
    fun testGetFruitsStatistics() {
        fruitRepository.saveAll(
            listOf(
                Fruit("apple"),
                Fruit("pear")
            )
        ).blockLast()

        webTestClient.get().uri("/fruits?name=banana")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.fruitsPerDay").isEmpty

        webTestClient.get().uri("/fruits?name=apple")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<FruitStatistics>()
            .consumeWith {
                val fruitsPerDay = it.responseBody?.fruitsPerDay
                assertThat(fruitsPerDay).hasSize(1)
            }
    }

    @Test
    fun testPostHappyFlow() {
        webTestClient.post().uri("/fruits")
            .contentType(APPLICATION_JSON)
            .bodyValue("{ \"name\": \"pineapple\" }")
            .exchange().expectStatus().isOk
            .expectBody().isEmpty

        StepVerifier.create(fruitRepository.findAll())
            .expectNextCount(1)
            .expectComplete()
            .verify()
    }

    @Test
    fun testPostWithErrors() {
        webTestClient.post().uri("/fruits")
            .contentType(APPLICATION_JSON)
            .bodyValue("{ bananas }")
            .exchange().expectStatus().isBadRequest
    }

    @Test
    fun testExternalServiceHappyFlow() {
        webTestClient.post().uri("/external")
            .contentType(APPLICATION_JSON)
            .exchange().expectStatus().isOk
            .expectBody<String>()
            .isEqualTo("watermelon")
    }

    @Test
    fun testExternalServiceUnreachable() {
        wireMock.stubFor(
            get(urlPathEqualTo("/api"))
                .willReturn(
                    aResponse().withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(SC_INTERNAL_SERVER_ERROR)
                )
        )

        webTestClient.post().uri("/external")
            .contentType(APPLICATION_JSON)
            .exchange().expectStatus().is5xxServerError
            .expectBody<String>()
            .isEqualTo("Unable to retrieve item")
    }

    private fun cleanDatabase() {
        fruitRepository.deleteAll().block()
    }

    companion object {
        const val DEFAULT_EXTERNAL_SERVICE_RESPONSE = "{\"items\":[\"watermelon\"]}"

        const val WIREMOCK_PORT = 19191
        private val wireMock: WireMockServer = WireMockServer(wireMockConfig().port(WIREMOCK_PORT))

        @JvmStatic
        @BeforeAll
        @Suppress("Unused")
        fun setup() {
            wireMock.start()
            configureFor("localhost", WIREMOCK_PORT)
            System.setProperty("wiremock.server.port", WIREMOCK_PORT.toString())
        }
    }
}
