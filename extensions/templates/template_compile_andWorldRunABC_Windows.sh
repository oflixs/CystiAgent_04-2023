#! /bin/bash

#To compile and run the CystiAgents Agent-Based Model in Mason

#This to compile
#make all --directory ../../../../../../

#This to run
~/jdk-11.0.11/bin/java -cp "./;./allJar/*" sim.app.cystiagents.CystiAgentsWorld simName timeMark
