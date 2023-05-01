#! /bin/bash

###############################################################
#Village 588

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs588.sh
cp -v test-runs_template.sh ./test-runs588.sh
cp -v para-runs_template.sh ./para-runs588.sh

sed -i 's/xxx/588/g' launch-runs588.sh
sed -i 's/xxx/588/g' test-runs588.sh
sed -i 's/xxx/588/g' para-runs588.sh

chmod 777 ./launch-runs588.sh
chmod 777 ./test-runs588.sh
chmod 777 ./para-runs588.sh

cp -v launch-runs588.sh ../Runs/
cp -v test-runs588.sh ../Runs/
cp -v para-runs588.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_588.params ../paramsFiles

cd ../Runs

sh ./launch-runs588.sh > ../out588.out & bg
###############################################################


