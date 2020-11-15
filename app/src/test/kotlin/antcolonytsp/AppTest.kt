package antcolonytsp

import java.io.File
import kotlin.math.sqrt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AppTest {
    @Test
    fun testIntegration() {
        testFramework(29, "wi29.tsp")
        testFramework(38, "dj38.tsp")
    }

    private fun testFramework(citiesCount: Int, fileName: String) {
        val circuit = Circuit(
            fileName = fileName,
            evaporationFactor = 0.05,
            transitionControl = 0.35,
            populationSize = 250,
            pheromoneMode = PheromoneMode.OFFLINE,
            iterations = 250,
            alpha = 0.5,
            beta = 1.0,
            startingPheromone = 1.0
        )
        val (bestCost, bestPath) = circuit.compute()

        val lines = ArrayList<String>()
        File(fileName).forEachLine {
            if (it.isNotBlank()) {
                lines.add(it)
            }
        }

        val cities = HashSet<Int>()
        var totalCost = 0.0
        var lastCityID = -1
        var lastXCoord = -1.0
        var lastYCoord = -1.0
        for (cityID in bestPath) {
            val line = lines[cityID - 1]
            val values = line.split(" ")
            val lineCityID = values[0].toInt()
            val xCoord = values[1].toDouble()
            val yCoord = values[2].toDouble()
            assertEquals(lineCityID, cityID)
            if (lastCityID != -1) {
                assertFalse(cities.contains(cityID))
                cities.add(cityID)
                val xDiff = xCoord - lastXCoord
                val yDiff = yCoord - lastYCoord
                totalCost += sqrt(xDiff * xDiff + yDiff * yDiff)
            }
            lastCityID = cityID
            lastXCoord = xCoord
            lastYCoord = yCoord
        }
        assertEquals(citiesCount, cities.size)
        assertEquals(totalCost, bestCost)
    }
}
