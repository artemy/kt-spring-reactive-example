package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface FruitRepository : R2dbcRepository<Fruit, Int>
