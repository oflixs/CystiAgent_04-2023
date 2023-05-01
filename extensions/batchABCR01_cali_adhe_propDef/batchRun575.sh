#! /bin/bash

###############################################################
#Village 575

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs575.sh
cp -v test-runs_template.sh ./test-runs575.sh
cp -v para-runs_template.sh ./para-runs575.sh

sed -i 's/xxx/575/g' launch-runs575.sh
sed -i 's/xxx/575/g' test-runs575.sh
sed -i 's/xxx/575/g' para-runs575.sh

chmod 777 ./launch-runs575.sh
chmod 777 ./test-runs575.sh
chmod 777 ./para-runs575.sh

cp -v launch-runs575.sh ../Runs/
cp -v test-runs575.sh ../Runs/
cp -v para-runs575.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_575.params ../paramsFiles

cd ../Runs

sh ./launch-runs575.sh > ../out575.out & bg
###############################################################


