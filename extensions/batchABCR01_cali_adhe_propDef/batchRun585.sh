#! /bin/bash

###############################################################
#Village 585

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs585.sh
cp -v test-runs_template.sh ./test-runs585.sh
cp -v para-runs_template.sh ./para-runs585.sh

sed -i 's/xxx/585/g' launch-runs585.sh
sed -i 's/xxx/585/g' test-runs585.sh
sed -i 's/xxx/585/g' para-runs585.sh

chmod 777 ./launch-runs585.sh
chmod 777 ./test-runs585.sh
chmod 777 ./para-runs585.sh

cp -v launch-runs585.sh ../Runs/
cp -v test-runs585.sh ../Runs/
cp -v para-runs585.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_585.params ../paramsFiles

cd ../Runs

sh ./launch-runs585.sh > ../out585.out & bg
###############################################################


