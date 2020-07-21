package antcolonytsp

import java.io.File
import kotlin.math.roundToInt

const val CITIES_SIZE = 29
const val ARTIFICIAL_PHEROMONE = 1.0
const val ALPHA = 0.5
const val BETA = 1.0
const val ITERATIONS = 250

fun main() {
    print("(median, arithmetic mean)\n\n")
    computeLogicGroup(true)
    computeLogicGroup(false)
}

internal fun computeLogicGroup(isPheromoneOnline: Boolean) {
    val listDouble = ArrayList<Any>()
    for (i in 5 until 100 step 5) {
        listDouble.add(i / 100.0)
    }
    computeSection("evaporation", isPheromoneOnline, listDouble)
    computeSection("transition", isPheromoneOnline, listDouble)
    val listInt = ArrayList<Any>()
    for (i in 25..500 step 25) {
        listInt.add(i)
    }
    computeSection("population", isPheromoneOnline, listInt)
}

internal fun computeSection(type: String, isPheromoneOnline: Boolean, list: ArrayList<Any>) {
    val onlineCode = if (isPheromoneOnline) "online" else "offline"
    val fileName = "${type}_${onlineCode}.csv"
    File(fileName).delete()
    File(fileName).appendText("sep=,\n")
    for (v in list) {
        var evaporation = 0.2
        var transition = 0.5
        var population = 200
        when (type) {
            "evaporation" -> if (v is Double) evaporation = v else throw TypeCastException()
            "transition" -> if (v is Double) transition = v else throw TypeCastException()
            "population" -> if (v is Int) population = v else throw TypeCastException()
        }
        val tuple = computeAverageCost(evaporation, transition, population, isPheromoneOnline)
        print("$type $onlineCode $v: $tuple\n")
        File(fileName).appendText("$v,${tuple.first}\n")
    }
    print("\n")
}

internal fun computeAverageCost(evaporationFactor: Double, transitionControl: Double,
                                populationSize: Int, isPheromoneOnline: Boolean): Pair<Int, Int> {
    var arithmeticMean = 0.0
    val computations = 15
    val allCosts = ArrayList<Int>()
    for (i in 0 until computations) {
        val circuit = Circuit()
        val currentCost = circuit.compute(evaporationFactor, transitionControl, populationSize, isPheromoneOnline)
        arithmeticMean += currentCost
        allCosts.add(currentCost.roundToInt())
    }
    allCosts.sort()
    return Pair(allCosts[computations / 2], (arithmeticMean / computations).roundToInt())
}
