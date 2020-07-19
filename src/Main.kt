import java.io.File
import java.lang.Math.random
import kotlin.IllegalStateException
import kotlin.collections.ArrayList
import kotlin.math.*

const val CITIES_SIZE = 29
const val ARTIFICIAL_PHEROMONE = 1.0
const val ALPHA = 0.5
const val BETA = 1.0
const val ITERATIONS = 250

internal class City(private val cityID: Int,
                    private val xCoord: Double,
                    private val yCoord: Double) {
    fun distanceFrom(city: City): Double {
        val xDiff = xCoord - city.xCoord
        val yDiff = yCoord - city.yCoord
        return sqrt(xDiff * xDiff + yDiff * yDiff)
    }

    fun pheromoneBetween(city: City, pheromone: Array<Array<Double>>): Double {
        val minCity = min(cityID, city.cityID)
        val maxCity = max(cityID, city.cityID)
        val localPheromone = pheromone[minCity][maxCity]
        if (localPheromone < 0.0) {
            throw IllegalStateException("Invalid pheromone access")
        }
        return localPheromone
    }

    fun addPheromone(other: City, pheromone: Array<Array<Double>>) {
        val min = if (cityID < other.cityID) this else other
        val max = if (cityID < other.cityID) other else this
        pheromone[min.cityID][max.cityID] += 1 / min.distanceFrom(max)
    }

    companion object {
        fun evaporate(evaporationFactor: Double, pheromone: Array<Array<Double>>) {
            for (y in 0 until CITIES_SIZE) {
                for (x in 0 until CITIES_SIZE) {
                    pheromone[y][x] *= (1 - evaporationFactor)
                }
            }
        }
    }
}

internal class Ant(private val initialCityID: Int, private val cities: ArrayList<City>) {
    private var currentCity = initialCityID
    private var allowableTravel = Array(CITIES_SIZE) { i -> i != initialCityID }
    private var cityHistory = arrayListOf(cities[initialCityID])

    fun move(transitionControl: Double, pheromone: Array<Array<Double>>) {
        if (random() < transitionControl) bestMove(pheromone) else rouletteMove(pheromone)
    }

    private fun bestMove(pheromone: Array<Array<Double>>) {
        var bestImportance = -1.0
        var index = -1
        for (i in 0 until CITIES_SIZE) {
            if (!allowableTravel[i]) {
                continue
            }
            val localPheromone = cities[currentCity].pheromoneBetween(cities[i], pheromone)
            val distance = cities[currentCity].distanceFrom(cities[i])
            val relativeImportance = localPheromone / distance.pow(BETA)
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
        val importance = Array(CITIES_SIZE) { 0.0 }
        var totalImportance = 0.0
        for (i in 0 until CITIES_SIZE) {
            if (!allowableTravel[i]) {
                continue
            }
            val localPheromone = cities[currentCity].pheromoneBetween(cities[i], pheromone)
            val distance = cities[currentCity].distanceFrom(cities[i])
            val relativeImportance = localPheromone.pow(ALPHA) / distance.pow(BETA)
            importance[i] = relativeImportance
            totalImportance += relativeImportance
        }
        if (totalImportance == 0.0) {
            throw IllegalStateException("Total importance is zero")
        }
        var i = 0
        while (true) {
            if (random() < importance[i] / totalImportance) {
                currentCity = i
                allowableTravel[i] = false
                cityHistory.add(cities[i])
                return
            }
            i = (i + 1) % CITIES_SIZE
        }
    }

    fun getCost(): Double {
        if (cityHistory.size != CITIES_SIZE) {
            throw IllegalStateException("Did not travel to all nodes")
        }
        var cost = 0.0
        cityHistory.add(cities[initialCityID])
        for (i in 0 until CITIES_SIZE) {
            cost += cityHistory[i].distanceFrom(cityHistory[i + 1])
        }
        return cost
    }

    fun getCityHistory(): ArrayList<City> {
        if (cityHistory.size != CITIES_SIZE + 1) {
            throw IllegalStateException("Did not travel to all nodes")
        }
        return ArrayList(cityHistory)
    }

    fun reset() {
        currentCity = initialCityID
        for (i in 0 until CITIES_SIZE) {
            allowableTravel[i] = (i != initialCityID)
        }
        cityHistory.clear()
        cityHistory.add(cities[initialCityID])
    }
}

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
