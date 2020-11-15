package antcolonytsp

import kotlin.math.pow

internal class Ant(
    private val initialCityID: Int,
    private val cities: ArrayList<City>,
    private val alpha: Double,
    private val beta: Double
) {
    private var currentCity = initialCityID
    private val allowableTravel = Array(cities.size) { i -> i != initialCityID }
    private val cityHistory = arrayListOf(cities[initialCityID])

    fun move(transitionControl: Double, pheromone: Array<Array<Double>>) {
        if (Math.random() < transitionControl) bestMove(pheromone) else rouletteMove(pheromone)
    }

    private fun bestMove(pheromone: Array<Array<Double>>) {
        var bestImportance = -1.0
        var index = -1
        for (i in 0 until cities.size) {
            if (!allowableTravel[i]) {
                continue
            }
            val localPheromone = cities[currentCity].pheromoneBetween(cities[i], pheromone)
            val distance = cities[currentCity].distanceFrom(cities[i])
            val relativeImportance = localPheromone / distance.pow(beta)
            if (relativeImportance > bestImportance) {
                bestImportance = relativeImportance
                index = i
            }
        }
        if (index < 0) {
            throw IllegalStateException("Index less than zero")
        }
        currentCity = index
        allowableTravel[index] = false
        cityHistory.add(cities[index])
    }

    private fun rouletteMove(pheromone: Array<Array<Double>>) {
        val importance = Array(cities.size) { 0.0 }
        var totalImportance = 0.0
        for (i in 0 until cities.size) {
            if (!allowableTravel[i]) {
                continue
            }
            val localPheromone = cities[currentCity].pheromoneBetween(cities[i], pheromone)
            val distance = cities[currentCity].distanceFrom(cities[i])
            val relativeImportance = localPheromone.pow(alpha) / distance.pow(beta)
            importance[i] = relativeImportance
            totalImportance += relativeImportance
        }
        if (totalImportance == 0.0) {
            throw IllegalStateException("Total importance is zero")
        }
        var i = 0
        while (true) {
            if (Math.random() < importance[i] / totalImportance) {
                currentCity = i
                allowableTravel[i] = false
                cityHistory.add(cities[i])
                return
            }
            i = (i + 1) % cities.size
        }
    }

    fun getCost(): Double {
        if (cityHistory.size != cities.size) {
            throw IllegalStateException("Did not travel to all nodes")
        }
        var cost = 0.0
        cityHistory.add(cities[initialCityID])
        for (i in 0 until cities.size) {
            cost += cityHistory[i].distanceFrom(cityHistory[i + 1])
        }
        return cost
    }

    fun getCityHistory(): ArrayList<City> {
        if (cityHistory.size != cities.size + 1) {
            throw IllegalStateException("Did not travel to all nodes")
        }
        return ArrayList(cityHistory)
    }

    fun reset() {
        currentCity = initialCityID
        for (i in 0 until cities.size) {
            allowableTravel[i] = (i != initialCityID)
        }
        cityHistory.clear()
        cityHistory.add(cities[initialCityID])
    }
}
