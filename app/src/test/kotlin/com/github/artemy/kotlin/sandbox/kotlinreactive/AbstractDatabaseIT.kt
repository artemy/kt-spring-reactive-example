package com.github.artemy.kotlin.sandbox.kotlinreactive

import org.junit.jupiter.api.BeforeAll
import org.springframework.context.ApplicationContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

open class AbstractDatabaseIT {
    companion object {
        lateinit var webTestClient: WebTestClient

        @BeforeAll
        @JvmStatic
        @Suppress("unused")
        fun setup(applicationContext: ApplicationContext) {
            webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .build()
        }

        @Container
        private val postgresContainer = KPostgresContainer("postgres:13-alpine")

        @JvmStatic
        @DynamicPropertySource
        @Suppress("unused")
        fun testProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { postgresContainer.jdbcUrl.replace("jdbc", "r2dbc") }
            registry.add("spring.r2dbc.username", postgresContainer::getUsername)
            registry.add("spring.r2dbc.password", postgresContainer::getPassword)
            registry.add("spring.flyway.url") { postgresContainer.jdbcUrl }
            registry.add("spring.flyway.user", postgresContainer::getUsername)
            registry.add("spring.flyway.password", postgresContainer::getPassword)
        }
    }

    class KPostgresContainer(imageName: String) : PostgreSQLContainer<KPostgresContainer>(imageName)
}
