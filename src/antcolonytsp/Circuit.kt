package antcolonytsp

import java.io.File
import kotlin.math.min

internal class Circuit {
    private val cities = load()
    private val pheromone = Array(CITIES_SIZE) { i ->
        Array(CITIES_SIZE) { j ->
            if (i < j) ARTIFICIAL_PHEROMONE else -1.0
        }
    }

    fun compute(evaporationFactor: Double, transitionControl: Double,
                populationSize: Int, isPheromoneOnline: Boolean): Double {
        var ants = Array(populationSize) { i -> Ant(i % CITIES_SIZE, cities) }.toList()
        var bestGlobalCost = Double.MAX_VALUE
        for (i in 0 until ITERATIONS) {
            var bestCurrentCost = Double.MAX_VALUE
            lateinit var bestCurrentPath: ArrayList<City>
            ants = ants.shuffled()
            for (ant in ants) {
                for (j in 0 until CITIES_SIZE - 1) {
                    ant.move(transitionControl, pheromone)
                }
                val currentCost = ant.getCost()
                val currentPath = ant.getCityHistory()
                if (currentCost < bestCurrentCost) {
                    bestCurrentCost = currentCost
                    bestCurrentPath = currentPath
                }
                ant.reset()
                if (isPheromoneOnline) {
                    evaporate(evaporationFactor)
                    for (j in 0 until CITIES_SIZE) {
                        currentPath[j].addPheromone(currentPath[j + 1], pheromone)
                    }
                }
            }
            if (!isPheromoneOnline) {
                evaporate(evaporationFactor)
                for (j in 0 until CITIES_SIZE) {
                    bestCurrentPath[j].addPheromone(bestCurrentPath[j + 1], pheromone)
                }
            }
            bestGlobalCost = min(bestGlobalCost, bestCurrentCost)
        }
        return bestGlobalCost
    }

    private fun evaporate(evaporationFactor: Double) {
        for (y in 0 until CITIES_SIZE) {
            for (x in 0 until CITIES_SIZE) {
                pheromone[y][x] *= (1 - evaporationFactor)
            }
        }
    }

    private fun load(): ArrayList<City> {
        val cities = ArrayList<City>()
        File("cities.txt").forEachLine {
            if (it.isNotBlank()) {
                val values = it.split(",")
                if (values.size != 3) {
                    throw IllegalStateException("City must have three attributes")
                }
                val city = City(values[0].toInt() - 1, values[1].toDouble(), values[2].toDouble())
                cities.add(city)
            }
        }
        if (cities.size != CITIES_SIZE) {
            throw IllegalStateException("Incorrect number of cities")
        }
        return cities
    }
}
