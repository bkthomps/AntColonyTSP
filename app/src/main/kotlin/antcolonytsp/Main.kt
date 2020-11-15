package antcolonytsp

import kotlinx.cli.*

enum class PheromoneMode { OFFLINE, ONLINE_DELAYED }

fun main(args: Array<String>) {
    val DEFAULT_EVAPORATION_FACTOR = 0.05
    val DEFAULT_TRANSITION_CONTROL = 0.35
    val DEFAULT_POPULATION_SIZE = 250
    val DEFAULT_PHEROMONE_MODE = PheromoneMode.OFFLINE
    val DEFAULT_ITERATIONS = 250
    val DEFAULT_ALPHA = 0.5
    val DEFAULT_BETA = 1.0
    val DEFAULT_STARTING_PHEROMONE = 1.0

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

    val iterations by parser.option(
        ArgType.Int,
        shortName = "i",
        fullName = "iterations",
        description = "Iterations"
    ).default(DEFAULT_ITERATIONS)

    val alpha by parser.option(
        ArgType.Double,
        shortName = "a",
        fullName = "alpha",
        description = "Alpha"
    ).default(DEFAULT_ALPHA)

    val beta by parser.option(
        ArgType.Double,
        shortName = "b",
        fullName = "beta",
        description = "Beta"
    ).default(DEFAULT_BETA)

    val startingPheromone by parser.option(
        ArgType.Double,
        shortName = "s",
        fullName = "starting",
        description = "Starting Pheromone"
    ).default(DEFAULT_STARTING_PHEROMONE)

    parser.parse(args)

    val circuit = Circuit(fileName, startingPheromone)
    val currentCost = circuit.compute(
        evaporationFactor = evaporationFactor,
        transitionControl = transitionControl,
        populationSize = populationSize,
        pheromoneMode = pheromoneMode,
        iterations = iterations,
        alpha = alpha,
        beta = beta
    )
    println(currentCost)
}
