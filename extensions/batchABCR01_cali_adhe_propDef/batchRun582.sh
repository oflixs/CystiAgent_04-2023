#! /bin/bash

###############################################################
#Village 582

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs582.sh
cp -v test-runs_template.sh ./test-runs582.sh
cp -v para-runs_template.sh ./para-runs582.sh

sed -i 's/xxx/582/g' launch-runs582.sh
sed -i 's/xxx/582/g' test-runs582.sh
sed -i 's/xxx/582/g' para-runs582.sh

chmod 777 ./launch-runs582.sh
chmod 777 ./test-runs582.sh
chmod 777 ./para-runs582.sh

cp -v launch-runs582.sh ../Runs/
cp -v test-runs582.sh ../Runs/
cp -v para-runs582.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_582.params ../paramsFiles

cd ../Runs

sh ./launch-runs582.sh > ../out582.out & bg
###############################################################


