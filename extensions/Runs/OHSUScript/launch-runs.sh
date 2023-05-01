#!/bin/bash

#---------------------------
#compile core ABM
cd ../../

~/jdk-16.0.2/bin/javac -cp "./allJar/*" -Xmaxerrs 5 -d .  *.java

#---------------------------
#comile extensions
cd extensions

~/jdk-16.0.2/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java

cd Runs

#---------------------------
#Remove old out files
rm -v ./Outs/*

#---------------------------
#Run extensions loop
#for i in {0..1000}
#for i in {0..2}
for i in {0..1500}
#for i in {2..500}
#for i in {0..600}
#for i in {601..999}
#for i in {1000..1500}
do export i

#   if [ ! -d ../outputs/ITNtoHotspots1/ ]; then
#      echo dir ITNtoHotspots1 created from script
#      mkdir -p ../outputs/ITNtoHotspots1/
#   fi

   sbatch -J t$i --open-mode=truncate -o ./Outs/run-$i.out -e ./Outs/run-$i.err  test-runs.sh

   sleep 4

done
