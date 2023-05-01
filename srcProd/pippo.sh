#! /bin/bash

#--------------------------------------------------
cp paramsFiles/batchR01/582.params paramsFiles/R01_coreInput.params
echo "running village 582"
nice -20 ./compile_andRun.sh > R01Outs/out582.out 


echo "batch done"
