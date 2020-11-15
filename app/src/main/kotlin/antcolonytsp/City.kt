package antcolonytsp

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

internal class City(private val cityID: Int, private val xCoord: Double, private val yCoord: Double) {
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
}
