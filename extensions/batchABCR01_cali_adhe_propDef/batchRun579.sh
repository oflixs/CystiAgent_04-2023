#! /bin/bash

###############################################################
#Village 579

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs579.sh
cp -v test-runs_template.sh ./test-runs579.sh
cp -v para-runs_template.sh ./para-runs579.sh

sed -i 's/xxx/579/g' launch-runs579.sh
sed -i 's/xxx/579/g' test-runs579.sh
sed -i 's/xxx/579/g' para-runs579.sh

chmod 777 ./launch-runs579.sh
chmod 777 ./test-runs579.sh
chmod 777 ./para-runs579.sh

cp -v launch-runs579.sh ../Runs/
cp -v test-runs579.sh ../Runs/
cp -v para-runs579.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_579.params ../paramsFiles

cd ../Runs

sh ./launch-runs579.sh > ../out579.out & bg
###############################################################


