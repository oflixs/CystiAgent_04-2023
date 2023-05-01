#! /bin/bash

###############################################################
#Village 587

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs587.sh
cp -v test-runs_template.sh ./test-runs587.sh
cp -v para-runs_template.sh ./para-runs587.sh

sed -i 's/xxx/587/g' launch-runs587.sh
sed -i 's/xxx/587/g' test-runs587.sh
sed -i 's/xxx/587/g' para-runs587.sh

chmod 777 ./launch-runs587.sh
chmod 777 ./test-runs587.sh
chmod 777 ./para-runs587.sh

cp -v launch-runs587.sh ../Runs/
cp -v test-runs587.sh ../Runs/
cp -v para-runs587.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_587.params ../paramsFiles

cd ../Runs

sh ./launch-runs587.sh > ../out587.out & bg
###############################################################


