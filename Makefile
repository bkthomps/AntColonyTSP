.DEFAULT_GOAL := run

run:
	java -jar antcolonytsp.jar

compile:
	kotlinc src/antcolonytsp/*.kt -include-runtime -d antcolonytsp.jar

clean:
	rm *.jar

clear:
	rm *.csv
