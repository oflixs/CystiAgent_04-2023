#! /bin/bash

###############################################################
#Village 572

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs572.sh
cp -v test-runs_template.sh ./test-runs572.sh
cp -v para-runs_template.sh ./para-runs572.sh

sed -i 's/xxx/572/g' launch-runs572.sh
sed -i 's/xxx/572/g' test-runs572.sh
sed -i 's/xxx/572/g' para-runs572.sh

chmod 777 ./launch-runs572.sh
chmod 777 ./test-runs572.sh
chmod 777 ./para-runs572.sh

cp -v launch-runs572.sh ../Runs/
cp -v test-runs572.sh ../Runs/
cp -v para-runs572.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_572.params ../paramsFiles

cd ../Runs

sh ./launch-runs572.sh > ../out572.out & bg
###############################################################


