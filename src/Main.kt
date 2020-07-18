import java.io.File
import java.lang.Math.random
import kotlin.IllegalStateException
import kotlin.collections.ArrayList
import kotlin.math.min
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

const val CITIES_SIZE = 29
const val ARTIFICIAL_PHEROMONE = 1.0
const val ANTS_SIZE = 6 * CITIES_SIZE
const val ALPHA = 0.5
const val BETA = 1.0
const val EVAPORATION_FACTOR = 0.2  // (0, 1], 1 means random
const val ITERATIONS = 250

internal class City(private val cityID: Int,
                    private val xCoord: Double,
                    private val yCoord: Double) {
    fun distanceFrom(city: City): Double {
        val xDiff = xCoord - city.xCoord
        val yDiff = yCoord - city.yCoord
        return sqrt(xDiff * xDiff + yDiff * yDiff)
    }

    fun pheromoneBetween(city: City): Double {
        val minCity = min(cityID, city.cityID)
        val maxCity = max(cityID, city.cityID)
        val localPheromone = pheromone[minCity][maxCity]
        if (localPheromone <= 0.0) {
            throw IllegalStateException("Invalid pheromone access")
        }
        return localPheromone
    }

    fun addPheromone(other: City) {
        val min = if (cityID < other.cityID) this else other
        val max = if (cityID < other.cityID) other else this
        pheromone[min.cityID][max.cityID] += 1 / min.distanceFrom(max)
    }

    companion object {
        var pheromone = Array(CITIES_SIZE) { i ->
            Array(CITIES_SIZE) { j ->
                if (i < j) ARTIFICIAL_PHEROMONE else -1.0
            }
        }

        fun evaporate() {
            for (y in 0 until CITIES_SIZE) {
                for (x in 0 until CITIES_SIZE) {
                    pheromone[y][x] *= (1 - EVAPORATION_FACTOR)
                }
            }
        }
    }
}

internal class Ant(private val initialCityID: Int, private val cities: ArrayList<City>) {
    private var currentCity = initialCityID
    private var allowableTravel = Array(CITIES_SIZE) { i -> i != initialCityID }
    private var cityHistory = arrayListOf(cities[initialCityID])

    fun move() {
        val importance = Array(CITIES_SIZE) { 0.0 }
        var totalImportance = 0.0
        for (i in 0 until CITIES_SIZE) {
            if (!allowableTravel[i]) {
                continue
            }
            val pheromone = cities[currentCity].pheromoneBetween(cities[i])
            val distance = cities[currentCity].distanceFrom(cities[i])
            val relativeImportance = pheromone.pow(ALPHA) / distance.pow(BETA)
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

fun main() {
    val cities = load()
    val ants = Array(ANTS_SIZE) { i -> Ant(i % CITIES_SIZE, cities) }
    var bestGlobalCost = Double.MAX_VALUE
    for (i in 0 until ITERATIONS) {
        var bestCurrentCost = Double.MAX_VALUE
        lateinit var bestCurrentPath: ArrayList<City>
        for (ant in ants) {
            for (j in 0 until CITIES_SIZE - 1) {
                ant.move()
            }
            val currentCost = ant.getCost()
            if (currentCost < bestCurrentCost) {
                bestCurrentCost = currentCost
                bestCurrentPath = ant.getCityHistory()
            }
            ant.reset()
        }
        City.evaporate()
        for (j in 0 until CITIES_SIZE) {
            bestCurrentPath[j].addPheromone(bestCurrentPath[j + 1])
        }
        bestGlobalCost = min(bestGlobalCost, bestCurrentCost)
    }
    print("Best cost is $bestGlobalCost")
}
