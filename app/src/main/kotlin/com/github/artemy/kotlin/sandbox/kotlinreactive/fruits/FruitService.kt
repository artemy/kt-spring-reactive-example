package com.github.artemy.kotlin.sandbox.kotlinreactive.fruits

import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.Fruit
import com.github.artemy.kotlin.sandbox.kotlinreactive.domain.FruitStatistics
import org.springframework.stereotype.Service

@Service
class FruitService(
    private val fruitRepository: FruitRepository,
    private val fruitStatisticsRepository: FruitStatisticsRepository
) {

    fun saveFruit(fruit: Fruit) = fruitRepository.save(fruit)

    fun getStatisticsOverall() =
        fruitStatisticsRepository.fruitPerDay().collectList().map { FruitStatistics(it) }

    fun getStatisticsForFruit(name: String) =
        fruitStatisticsRepository.fruitPerDayForFruit(name).collectList().map { FruitStatistics(it) }
}
