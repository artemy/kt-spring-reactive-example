package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitStatistics
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.stereotype.Service

@Service
class FruitService(
    private val fruitRepository: FruitRepository,
    private val fruitStatisticsRepository: FruitStatisticsRepository
) {

    fun saveFruit(fruit: Fruit) = fruitRepository.save(fruit)

    suspend fun getStatisticsOverall() =
        FruitStatistics(
            fruitStatisticsRepository.fruitPerDay().collectList().awaitFirstOrDefault(emptyList())
        )

    suspend fun getStatisticsForFruit(name: String) = FruitStatistics(
        fruitStatisticsRepository.fruitPerDayForFruit(name).collectList()
            .awaitFirstOrDefault(emptyList())
    )
}
