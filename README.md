# AntColonyTSP
An approximation to the travelling salesperson problem using an ant colony system.

## Setup
If you do not have gradle, run `./gradlew` on MacOS or Linux, or `gradlew.bat` on Windows.

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
* `-i or --iterations` which specifies the iterations count (must be 1 or greater)
* `-a or --alpha` which specifies alpha (must be 0.0 or greater)
* `-b or --beta` which specifies beta (must be 0.0 or greater)
* `-s or --starting` which specifies the starting pheromone (must be 0.0 or greater)

Thus, if I wanted to run this program, I could run:
```
gradle run --args="-f cities.txt -e 0.05 -t 0.35 -p 250 -m offline -i 250 -a 0.5 -b 1.0 -s 1.0"
```

Or, if I wanted to rely on defaults, I could run:
```
gradle run --args="-f cities.txt"
```
