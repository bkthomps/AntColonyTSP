package antcolonytsp

import kotlinx.cli.*

const val ITERATIONS = 250
const val ALPHA = 0.5
const val BETA = 1.0
const val ARTIFICIAL_PHEROMONE = 1.0

enum class PheromoneMode { OFFLINE, ONLINE_DELAYED }

fun main(args: Array<String>) {
    val DEFAULT_EVAPORATION_FACTOR = 0.05
    val DEFAULT_TRANSITION_CONTROL = 0.35
    val DEFAULT_POPULATION_SIZE = 250
    val DEFAULT_PHEROMONE_MODE = PheromoneMode.OFFLINE

    val parser = ArgParser("gradle run --args=\"<arguments>\", using the following")

    val fileName by parser.option(
        ArgType.String,
        shortName = "f",
        fullName = "file",
        description = "Cities File Name"
    ).required()

    val evaporationFactor by parser.option(
        ArgType.Double,
        shortName = "e",
        fullName = "evaporation",
        description = "Evaporation Factor"
    ).default(DEFAULT_EVAPORATION_FACTOR)

    val transitionControl by parser.option(
        ArgType.Double,
        shortName = "t",
        fullName = "transition",
        description = "Transition Control"
    ).default(DEFAULT_TRANSITION_CONTROL)

    val populationSize by parser.option(
        ArgType.Int,
        shortName = "p",
        fullName = "population",
        description = "Population Size"
    ).default(DEFAULT_POPULATION_SIZE)

    val pheromoneMode by parser.option(
        ArgType.Choice<PheromoneMode>(),
        shortName = "m",
        fullName = "mode",
        description = "Pheromone Mode"
    ).default(DEFAULT_PHEROMONE_MODE)

    parser.parse(args)

    val circuit = Circuit()
    val currentCost = circuit.compute(evaporationFactor, transitionControl, populationSize, pheromoneMode)
    println(currentCost)
}
