#! /bin/bash

#--------------------------------------------------
cp paramsFiles/batchR01/578.params paramsFiles/R01_coreInput.params
echo "running village 578"
nice -20 ./compile_andRun.sh > R01Outs/out578.out 

#--------------------------------------------------
cp paramsFiles/batchR01/579.params paramsFiles/R01_coreInput.params
echo "running village 579"
nice -20 ./compile_andRun.sh > R01Outs/out579.out 

#--------------------------------------------------
cp paramsFiles/batchR01/580.params paramsFiles/R01_coreInput.params
echo "running village 580"
nice -20 ./compile_andRun.sh > R01Outs/out580.out 

#--------------------------------------------------
cp paramsFiles/batchR01/581.params paramsFiles/R01_coreInput.params
echo "running village 581"
nice -20 ./compile_andRun.sh > R01Outs/out581.out 

#--------------------------------------------------
cp paramsFiles/batchR01/582.params paramsFiles/R01_coreInput.params
echo "running village 582"
nice -20 ./compile_andRun.sh > R01Outs/out582.out 

#--------------------------------------------------
cp paramsFiles/batchR01/583.params paramsFiles/R01_coreInput.params
echo "running village 583"
nice -20 ./compile_andRun.sh > R01Outs/out583.out 

#--------------------------------------------------
cp paramsFiles/batchR01/584.params paramsFiles/R01_coreInput.params
echo "running village 584"
nice -20 ./compile_andRun.sh > R01Outs/out584.out 

#--------------------------------------------------
cp paramsFiles/batchR01/585.params paramsFiles/R01_coreInput.params
echo "running village 585"
nice -20 ./compile_andRun.sh > R01Outs/out585.out 

echo "batch done"
