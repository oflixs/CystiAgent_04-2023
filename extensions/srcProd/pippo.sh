#! /bin/bash

cp -v ./compile_andRun.sh ./compile_andRun.shold
cp -v paramsFiles/batchR01/compile_andRun.sh ./compile_andRun.sh

#--------------------------------------------------
cp paramsFiles/batchR01/563.params paramsFiles/extensionsOutcomes_R01.params
echo "running village 563"
nice -20 ./compile_andRun.sh > R01Outs/out563.out 

cp -v ./compile_andRun.shold ./compile_andRun.sh

echo "batch done"
