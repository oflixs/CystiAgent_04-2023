#! /bin/bash

###############################################################
#Village 583

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs583.sh
cp -v test-runs_template.sh ./test-runs583.sh
cp -v para-runs_template.sh ./para-runs583.sh

sed -i 's/xxx/583/g' launch-runs583.sh
sed -i 's/xxx/583/g' test-runs583.sh
sed -i 's/xxx/583/g' para-runs583.sh

chmod 777 ./launch-runs583.sh
chmod 777 ./test-runs583.sh
chmod 777 ./para-runs583.sh

cp -v launch-runs583.sh ../Runs/
cp -v test-runs583.sh ../Runs/
cp -v para-runs583.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_583.params ../paramsFiles

cd ../Runs

sh ./launch-runs583.sh > ../out583.out & bg
###############################################################


