#!/bin/bash
for i in {1..20}
do
	echo "Instance $i"
	java -jar CVRPTW.jar Instances/Instance$i.txt Results/Results_Instance$i.txt
	python Results/Validator/SolutionCVRPTWUI.py -s Results/Results_Instance$i.txt -i Instances/Instance$i.txt
done