package com.github.artemy.kotlin.sandbox.kotlinreactive.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant

@Table("fruits")
data class Fruit(
    @Id
    val id: Long? = null,

    val name: String,
    val datetime: Timestamp = Timestamp.from(Instant.now())
) {

    constructor(name: String) : this(name, Timestamp.from(Instant.now()))
    constructor(name: String, datetime: Timestamp) : this(null, name, datetime)
}

data class FruitRequest(val name: String)

data class FruitDay(val date: Date, val count: Int)

data class FruitStatistics(val fruitsPerDay: List<FruitDay>)

data class ExternalServiceResponse(val items: List<String>)
