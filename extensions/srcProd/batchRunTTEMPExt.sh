#! /bin/bash

cp paramsFiles/batchTTEMP/compile_andRun.sh ./compile_andRun.sh

#--------------------------------------------------
cp paramsFiles/batchTTEMP/507.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 507"
nice -20 ./compile_andRun.sh > TTEMPOuts/out507.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/510.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 510"
nice -20 ./compile_andRun.sh > TTEMPOuts/out510.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/515.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 515"
nice -20 ./compile_andRun.sh > TTEMPOuts/out515.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/517.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 517"
nice -20 ./compile_andRun.sh > TTEMPOuts/out517.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/566.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 566"
nice -20 ./compile_andRun.sh > TTEMPOuts/out566.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/567.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 567"
nice -20 ./compile_andRun.sh > TTEMPOuts/out567.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/568.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 568"
nice -20 ./compile_andRun.sh > TTEMPOuts/out568.out 

#--------------------------------------------------
cp paramsFiles/batchTTEMP/569.params paramsFiles/extensionsOutcomes_TTEMP.params
echo "running village 569"
nice -20 ./compile_andRun.sh > TTEMPOuts/out569.out 


