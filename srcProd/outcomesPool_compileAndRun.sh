#! /bin/bash

#To compile and run the CystiAgents Agent-Based Model in Mason

echo ------ compiling ----------------------
echo ......
#This to compile
#~/jdk-11.0.11/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java
echo ------ end compiling ------------------

#This to run
~/jdk-11.0.11/bin/java -cp "./;./allJar/*" sim.app.cystiagents.CystiAgentsWorld R01
