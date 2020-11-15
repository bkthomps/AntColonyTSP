package antcolonytsp

import java.io.File
import kotlin.math.min

internal class Circuit {
    private val cities = load()
    private val pheromone = Array(cities.size) { i ->
        Array(cities.size) { j ->
            if (i < j) ARTIFICIAL_PHEROMONE else -1.0
        }
    }

    fun compute(evaporationFactor: Double, transitionControl: Double,
                populationSize: Int, pheromoneMode: PheromoneMode): Double {
        var ants = Array(populationSize) { i -> Ant(i % cities.size, cities) }.toList()
        var bestGlobalCost = Double.MAX_VALUE
        for (i in 0 until ITERATIONS) {
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
        return cities
    }
}
