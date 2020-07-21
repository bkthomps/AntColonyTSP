package antcolonytsp

import java.io.File
import kotlin.math.min
import kotlin.math.roundToInt

const val CITIES_SIZE = 29
const val ARTIFICIAL_PHEROMONE = 1.0
const val ALPHA = 0.5
const val BETA = 1.0
const val ITERATIONS = 250

fun main() {
    alternateValues(true)
    alternateValues(false)
}

internal fun alternateValues(isPheromoneOnline: Boolean) {
    print("${computeMedianCost(0.1, 0.5, 200, isPheromoneOnline)}\n")
    print("${computeMedianCost(0.2, 0.5, 200, isPheromoneOnline)}\n")
    print("${computeMedianCost(0.5, 0.5, 200, isPheromoneOnline)}\n\n")

    print("${computeMedianCost(0.2, 0.2, 200, isPheromoneOnline)}\n")
    print("${computeMedianCost(0.2, 0.5, 200, isPheromoneOnline)}\n")
    print("${computeMedianCost(0.2, 0.8, 200, isPheromoneOnline)}\n\n")

    print("${computeMedianCost(0.2, 0.5, 50, isPheromoneOnline)}\n")
    print("${computeMedianCost(0.2, 0.5, 200, isPheromoneOnline)}\n\n")
}

internal fun computeMedianCost(evaporationFactor: Double, transitionControl: Double,
                               populationSize: Int, isPheromoneOnline: Boolean): Int {
    val computations = 9
    val allCosts = ArrayList<Int>()
    for (i in 0 until computations) {
        val currentCost =
                computeCost(evaporationFactor, transitionControl, populationSize, isPheromoneOnline)
        allCosts.add(currentCost.roundToInt())
    }
    allCosts.sort()
    return allCosts[computations / 2]
}

internal fun computeCost(evaporationFactor: Double, transitionControl: Double,
                         populationSize: Int, isPheromoneOnline: Boolean): Double {
    val cities = load()
    val pheromone = Array(CITIES_SIZE) { i ->
        Array(CITIES_SIZE) { j ->
            if (i < j) ARTIFICIAL_PHEROMONE else -1.0
        }
    }
    val ants = Array(populationSize) { i -> Ant(i % CITIES_SIZE, cities) }
    var bestGlobalCost = Double.MAX_VALUE
    for (i in 0 until ITERATIONS) {
        var bestCurrentCost = Double.MAX_VALUE
        lateinit var bestCurrentPath: ArrayList<City>
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
                City.evaporate(evaporationFactor, pheromone)
                for (j in 0 until CITIES_SIZE) {
                    currentPath[j].addPheromone(currentPath[j + 1], pheromone)
                }
            }
        }
        if (!isPheromoneOnline) {
            City.evaporate(evaporationFactor, pheromone)
            for (j in 0 until CITIES_SIZE) {
                bestCurrentPath[j].addPheromone(bestCurrentPath[j + 1], pheromone)
            }
        }
        bestGlobalCost = min(bestGlobalCost, bestCurrentCost)
    }
    return bestGlobalCost
}

internal fun load(): ArrayList<City> {
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
