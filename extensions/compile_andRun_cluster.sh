#! /bin/bash
echo ------ compiling ----------------------

#~/jdk1.8.0_131/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java
/hpc/home/fp36/jdk-16.0.2/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java

echo ...

echo ------ end compilation ----------------
echo 

#/hpc/home/fp36/jdk-16.0.2/bin/java -cp "./:../allJar/*" extensions.Extensions ABCCystiHumans.params
/hpc/home/fp36/jdk-16.0.2/bin/java -cp "./:../allJar/*" extensions.Extensions extensionsABC_TTEMP_transmission_analysis.params
