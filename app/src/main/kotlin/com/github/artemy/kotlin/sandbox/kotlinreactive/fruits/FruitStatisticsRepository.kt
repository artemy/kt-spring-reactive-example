package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitDay
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface FruitStatisticsRepository : R2dbcRepository<FruitDay, Int> {

    @Query(
        "SELECT f.datetime::date AS date, count(*) AS count " +
                "FROM fruits f " +
                "WHERE f.datetime >= CURRENT_DATE - INTERVAL '1' MONTH " +
                "GROUP BY date ORDER BY date DESC"
    )
    fun fruitPerDay(): Flux<FruitDay>

    @Query(
        "SELECT f.datetime::date AS date, count(*) AS count " +
                "FROM fruits f " +
                "WHERE f.datetime >= CURRENT_DATE - INTERVAL '1' MONTH " +
                "AND f.name = :name " +
                "GROUP BY date ORDER BY date DESC"
    )
    fun fruitPerDayForFruit(name: String): Flux<FruitDay>
}
