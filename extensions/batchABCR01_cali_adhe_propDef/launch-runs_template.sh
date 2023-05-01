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
#rm -v ./Outs/*

#---------------------------
#Run extensions loop
#for i in {0..1500}
for i in {0..65}
do export i

#   if [ ! -d ../outputs/ITNtoHotspots1/ ]; then
#      echo dir ITNtoHotspots1 created from script
#      mkdir -p ../outputs/ITNtoHotspots1/
#   fi

   sbatch -J xxx-$i --open-mode=truncate -o ./Outs/runxxx-$i.out -e ./Outs/runxxx-$i.err  test-runsxxx.sh

   sleep 4

done
