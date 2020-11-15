package antcolonytsp

import java.io.File
import kotlin.math.min

internal class Circuit(private val fileName: String, private val startingPheromone: Double) {
    private val cities = load()
    private val pheromone = Array(cities.size) { i ->
        Array(cities.size) { j ->
            if (i < j) startingPheromone else -1.0
        }
    }

    init {
        if (startingPheromone < 0.0) {
            throw IllegalArgumentException("Starting pheromone must be 0.0 or greater")
        }
    }

    fun compute(
        evaporationFactor: Double,
        transitionControl: Double,
        populationSize: Int,
        pheromoneMode: PheromoneMode,
        iterations: Int,
        alpha: Double,
        beta: Double
    ): Double {
        if (evaporationFactor < 0.0 || evaporationFactor > 1.0) {
            throw IllegalArgumentException("Evaporation factor must be in [0.0, 1.0]")
        }
        if (transitionControl < 0.0 || transitionControl > 1.0) {
            throw IllegalArgumentException("Transition control must be in [0.0, 1.0]")
        }
        if (populationSize < 1) {
            throw IllegalArgumentException("Population size must be 1 or greater")
        }
        if (iterations < 1) {
            throw IllegalArgumentException("Iteration count must be 1 or greater")
        }
        if (alpha < 0.0) {
            throw IllegalArgumentException("Alpha must be 0.0 or greater")
        }
        if (beta < 0.0) {
            throw IllegalArgumentException("Beta must be 0.0 or greater")
        }
        var ants = Array(populationSize) { i -> Ant(i % cities.size, cities, alpha, beta) }.toList()
        var bestGlobalCost = Double.MAX_VALUE
        for (i in 0 until iterations) {
            var bestCurrentCost = Double.MAX_VALUE
            lateinit var bestCurrentPath: ArrayList<City>
            ants = ants.shuffled()
            for (ant in ants) {
                for (j in 0 until cities.size - 1) {
                    ant.move(transitionControl, pheromone)
                }
                val currentCost = ant.getCost()
                val currentPath = ant.getCityHistory()
                if (currentCost < bestCurrentCost) {
                    bestCurrentCost = currentCost
                    bestCurrentPath = currentPath
                }
                ant.reset()
                if (pheromoneMode == PheromoneMode.ONLINE_DELAYED) {
                    evaporate(evaporationFactor)
                    for (j in 0 until cities.size) {
                        currentPath[j].addPheromone(currentPath[j + 1], pheromone)
                    }
                }
            }
            if (pheromoneMode == PheromoneMode.OFFLINE) {
                evaporate(evaporationFactor)
                for (j in 0 until cities.size) {
                    bestCurrentPath[j].addPheromone(bestCurrentPath[j + 1], pheromone)
                }
            }
            bestGlobalCost = min(bestGlobalCost, bestCurrentCost)
        }
        return bestGlobalCost
    }

    private fun evaporate(evaporationFactor: Double) {
        for (y in 0 until cities.size) {
            for (x in 0 until cities.size) {
                pheromone[y][x] *= (1 - evaporationFactor)
            }
        }
    }

    private fun load(): ArrayList<City> {
        val cities = ArrayList<City>()
        val file = File(fileName)
        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist")
        }
        file.forEachLine {
            if (it.isNotBlank()) {
                val values = it.split(",")
                if (values.size != 3) {
                    throw IllegalStateException("City must have three attributes")
                }
                val city = City(values[0].toInt() - 1, values[1].toDouble(), values[2].toDouble())
                cities.add(city)
            }
        }
        if (cities.size == 0) {
            throw IllegalStateException("Could not load cities")
        }
        return cities
    }
}
