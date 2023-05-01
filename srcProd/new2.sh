#! /bin/bash

#--------------------------------------------------
cp paramsFiles/batchR01/586.params paramsFiles/R01_coreInput.params
echo "running village 586"
nice -20 ./compile_andRun.sh > R01Outs/out586.out 

#--------------------------------------------------
cp paramsFiles/batchR01/587.params paramsFiles/R01_coreInput.params
echo "running village 587"
nice -20 ./compile_andRun.sh > R01Outs/out587.out 

#--------------------------------------------------
cp paramsFiles/batchR01/588.params paramsFiles/R01_coreInput.params
echo "running village 588"
nice -20 ./compile_andRun.sh > R01Outs/out588.out 

#--------------------------------------------------
cp paramsFiles/batchR01/589.params paramsFiles/R01_coreInput.params
echo "running village 589"
nice -20 ./compile_andRun.sh > R01Outs/out589.out 

#--------------------------------------------------
cp paramsFiles/batchR01/590.params paramsFiles/R01_coreInput.params
echo "running village 590"
nice -20 ./compile_andRun.sh > R01Outs/out590.out 

#--------------------------------------------------
cp paramsFiles/batchR01/591.params paramsFiles/R01_coreInput.params
echo "running village 591"
nice -20 ./compile_andRun.sh > R01Outs/out591.out 

echo "batch done"
