#! /bin/bash

###############################################################
#Village 586

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs586.sh
cp -v test-runs_template.sh ./test-runs586.sh
cp -v para-runs_template.sh ./para-runs586.sh

sed -i 's/xxx/586/g' launch-runs586.sh
sed -i 's/xxx/586/g' test-runs586.sh
sed -i 's/xxx/586/g' para-runs586.sh

chmod 777 ./launch-runs586.sh
chmod 777 ./test-runs586.sh
chmod 777 ./para-runs586.sh

cp -v launch-runs586.sh ../Runs/
cp -v test-runs586.sh ../Runs/
cp -v para-runs586.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_586.params ../paramsFiles

cd ../Runs

sh ./launch-runs586.sh > ../out586.out & bg
###############################################################


