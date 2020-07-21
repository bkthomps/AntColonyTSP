package antcolonytsp

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
        val circuit = Circuit()
        val currentCost = circuit.compute(evaporationFactor, transitionControl, populationSize, isPheromoneOnline)
        allCosts.add(currentCost.roundToInt())
    }
    allCosts.sort()
    return allCosts[computations / 2]
}
