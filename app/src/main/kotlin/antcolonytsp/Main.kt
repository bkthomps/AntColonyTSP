package antcolonytsp

import kotlinx.cli.*
import java.io.File

enum class PheromoneMode { OFFLINE, ONLINE_DELAYED }

fun main(args: Array<String>) {
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
    ).default(0.05)

    val transitionControl by parser.option(
        ArgType.Double,
        shortName = "t",
        fullName = "transition",
        description = "Transition Control"
    ).default(0.35)

    val populationSize by parser.option(
        ArgType.Int,
        shortName = "p",
        fullName = "population",
        description = "Population Size"
    ).default(250)

    val pheromoneMode by parser.option(
        ArgType.Choice<PheromoneMode>(),
        shortName = "m",
        fullName = "mode",
        description = "Pheromone Mode"
    ).default(PheromoneMode.OFFLINE)

    val iterations by parser.option(
        ArgType.Int,
        shortName = "i",
        fullName = "iterations",
        description = "Iterations"
    ).default(250)

    val alpha by parser.option(
        ArgType.Double,
        shortName = "a",
        fullName = "alpha",
        description = "Alpha"
    ).default(0.5)

    val beta by parser.option(
        ArgType.Double,
        shortName = "b",
        fullName = "beta",
        description = "Beta"
    ).default(1.0)

    val startingPheromone by parser.option(
        ArgType.Double,
        shortName = "s",
        fullName = "starting",
        description = "Starting Pheromone"
    ).default(1.0)

    parser.parse(args)

    val circuit = Circuit(
        fileName = fileName,
        evaporationFactor = evaporationFactor,
        transitionControl = transitionControl,
        populationSize = populationSize,
        pheromoneMode = pheromoneMode,
        iterations = iterations,
        alpha = alpha,
        beta = beta,
        startingPheromone = startingPheromone
    )
    val (bestCost, bestPath) = circuit.compute()
    val file = File("result_" + fileName)
    file.delete()
    file.appendText("Cost: $bestCost\nPath:\n")
    for (city in bestPath) {
        file.appendText("$city\n")
    }
}
