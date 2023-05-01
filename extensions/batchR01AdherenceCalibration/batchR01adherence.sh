#! /bin/bash

#####################################################
#Village 563

cp -v extensionsABC_R01_adherence.params  ./tmp.params

sed -i 's/villagesNames R01/villagesNames R01_563/g' tmp.params

cp tmp.params ../paramsFiles/extensionsABC_R01_adherence.params

cd ../

sh ./compile_andRun.sh > batchR01AdherenceCalibration/563.out & bg

cd batchR01AdherenceCalibration

wait

mkdir -v results_R01_563

cp -vr ../outputs/R01ABC_R01_stage* results_R01_563/

rm -vr ../outputs/R01ABC_R01_stage* 
#####################################################


#####################################################
#Village 570

cp -v extensionsABC_R01_adherence.params  ./tmp.params

sed -i 's/villagesNames R01/villagesNames R01_570/g' tmp.params

cp tmp.params ../paramsFiles/extensionsABC_R01_adherence.params

cd ../

sh ./compile_andRun.sh > batchR01AdherenceCalibration/570.out & bg

cd batchR01AdherenceCalibration

wait

mkdir -v results_R01_570

cp -vr ../outputs/R01ABC_R01_stage* results_R01_570/

rm -vr ../outputs/R01ABC_R01_stage* 
#####################################################


