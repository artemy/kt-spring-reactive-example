package com.github.artemy.kotlin.sandbox.externalstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import wiremock.com.google.common.net.HttpHeaders.CONTENT_TYPE
import wiremock.org.apache.http.HttpStatus.SC_OK

@SpringBootApplication
class ExternalStubApp(
    @Value("\${http.port:8080}")
    private val httpPort: Int
) : CommandLineRunner {

    override fun run(vararg args: String) {

        val wireMockServer = WireMockServer(wireMockConfig().port(httpPort).disableRequestJournal())

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/api"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(SC_OK)
                        .withBody(DEFAULT_RESPONSE)
                )
        )
        wireMockServer.start()
    }

    companion object {
        var DEFAULT_RESPONSE =
            "{\"items\": [\"banana\"]}"

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ExternalStubApp::class.java, *args)
        }
    }
}
