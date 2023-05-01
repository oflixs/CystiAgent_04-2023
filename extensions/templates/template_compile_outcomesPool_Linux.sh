#! /bin/bash

#To compile and run the CystiAgents Agent-Based Model in Mason

#echo ------ compiling ----------------------
#echo ......
#This to compile
#echo ------ end compiling ------------------

#This to run
#/hpc/home/fp36/jdk-16.0.2/bin/java -classpath './:./allJar/*' sim.app.cystiagents.CystiAgentsWorld simName
~/jdk-16.0.2/bin/java -classpath './:./allJar/*' sim.app.cystiagents.CystiAgentsWorld simName
