#! /bin/bash

###############################################################
#Village 578

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs578.sh
cp -v test-runs_template.sh ./test-runs578.sh
cp -v para-runs_template.sh ./para-runs578.sh

sed -i 's/xxx/578/g' launch-runs578.sh
sed -i 's/xxx/578/g' test-runs578.sh
sed -i 's/xxx/578/g' para-runs578.sh

chmod 777 ./launch-runs578.sh
chmod 777 ./test-runs578.sh
chmod 777 ./para-runs578.sh

cp -v launch-runs578.sh ../Runs/
cp -v test-runs578.sh ../Runs/
cp -v para-runs578.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_578.params ../paramsFiles

cd ../Runs

sh ./launch-runs578.sh > ../out578.out & bg
###############################################################


