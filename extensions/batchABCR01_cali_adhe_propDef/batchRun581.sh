#! /bin/bash

###############################################################
#Village 581

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs581.sh
cp -v test-runs_template.sh ./test-runs581.sh
cp -v para-runs_template.sh ./para-runs581.sh

sed -i 's/xxx/581/g' launch-runs581.sh
sed -i 's/xxx/581/g' test-runs581.sh
sed -i 's/xxx/581/g' para-runs581.sh

chmod 777 ./launch-runs581.sh
chmod 777 ./test-runs581.sh
chmod 777 ./para-runs581.sh

cp -v launch-runs581.sh ../Runs/
cp -v test-runs581.sh ../Runs/
cp -v para-runs581.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_581.params ../paramsFiles

cd ../Runs

sh ./launch-runs581.sh > ../out581.out & bg
###############################################################


