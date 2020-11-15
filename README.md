# AntColonyTSP
An approximation to the travelling salesperson problem using an ant colony system.

## Setup
If you do not have gradle, run `./gradlew` on MacOS or Linux, or `gradlew.bat` on Windows.

Run `gradle build` to build and `gradle clean` to clean the built artifacts.

To run the AntColonyTSP, run `gradle run --args="<arguments>"` where the possible arguments are (all arguments contain
reasonable defaults which can be omitted, with the only required argument being the file name):
* `-f or --file` the file name of the travelling salesman problem data file, located in the app directory, and where
  more data files can be found [here](http://www.math.uwaterloo.ca/tsp/data/)
* `-e or --evaporation` the evaporation factor, which is the factor of how much pheromone gets evaporated each time
  an evaporation occurs (must be between 0.0 and 1.0)
* `-t or --transition` the transition control factor, which is how often ants will pick the path with the highest
  pheromone count versus using a roulette wheel (must be between 0.0 and 1.0)
* `-p or --population` which is the ant population size (must be 1 or greater)
* `-m or --mode` which specifies either offline mode or online_delayed mode

Thus, if I wanted to build and run this program, I could first run `gradle build` and then to run the program I could:
```
gradle run --args="-f cities.txt -e 0.05 -t 0.35 -p 250 -m offline"
```

Or, if I wanted to rely on defaults, I could first run `gradle build` and then run:
```
gradle run --args="-f cities.txt"
```