#! /bin/bash

###############################################################
#Village 580

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs580.sh
cp -v test-runs_template.sh ./test-runs580.sh
cp -v para-runs_template.sh ./para-runs580.sh

sed -i 's/xxx/580/g' launch-runs580.sh
sed -i 's/xxx/580/g' test-runs580.sh
sed -i 's/xxx/580/g' para-runs580.sh

chmod 777 ./launch-runs580.sh
chmod 777 ./test-runs580.sh
chmod 777 ./para-runs580.sh

cp -v launch-runs580.sh ../Runs/
cp -v test-runs580.sh ../Runs/
cp -v para-runs580.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_580.params ../paramsFiles

cd ../Runs

sh ./launch-runs580.sh > ../out580.out & bg
###############################################################


