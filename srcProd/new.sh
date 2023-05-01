#! /bin/bash

#--------------------------------------------------
cp paramsFiles/batchR01/563.params paramsFiles/R01_coreInput.params
echo "running village 563"
nice -20 ./compile_andRun.sh > R01Outs/out563.out 

#--------------------------------------------------
cp paramsFiles/batchR01/570.params paramsFiles/R01_coreInput.params
echo "running village 570"
nice -20 ./compile_andRun.sh > R01Outs/out570.out 

#--------------------------------------------------
cp paramsFiles/batchR01/571.params paramsFiles/R01_coreInput.params
echo "running village 571"
nice -20 ./compile_andRun.sh > R01Outs/out571.out 

#--------------------------------------------------
cp paramsFiles/batchR01/572.params paramsFiles/R01_coreInput.params
echo "running village 572"
nice -20 ./compile_andRun.sh > R01Outs/out572.out 

#--------------------------------------------------
cp paramsFiles/batchR01/573.params paramsFiles/R01_coreInput.params
echo "running village 573"
nice -20 ./compile_andRun.sh > R01Outs/out573.out 

#--------------------------------------------------
cp paramsFiles/batchR01/574.params paramsFiles/R01_coreInput.params
echo "running village 574"
nice -20 ./compile_andRun.sh > R01Outs/out574.out 

#--------------------------------------------------
cp paramsFiles/batchR01/575.params paramsFiles/R01_coreInput.params
echo "running village 575"
nice -20 ./compile_andRun.sh > R01Outs/out575.out 

#--------------------------------------------------
cp paramsFiles/batchR01/576.params paramsFiles/R01_coreInput.params
echo "running village 576"
nice -20 ./compile_andRun.sh > R01Outs/out576.out 

#--------------------------------------------------
cp paramsFiles/batchR01/577.params paramsFiles/R01_coreInput.params
echo "running village 577"
nice -20 ./compile_andRun.sh > R01Outs/out577.out 

echo "batch done"
