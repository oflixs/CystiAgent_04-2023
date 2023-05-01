#! /bin/bash

#To compile and run the CystiAgents Agent-Based Model in Mason

echo ------ compiling ----------------------
echo ......
#This to compile
#~/jdk1.8.0_131/bin/javac -cp "./allJar/*" -Xmaxerrs 5 -d .  *.java
/hpc/home/fp36/jdk-16.0.2/bin/javac -cp "./allJar/*" -Xmaxerrs 5 -d .  *.java
echo ------ end compiling ------------------
