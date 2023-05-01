#! /bin/bash

###############################################################
#Village 584

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs584.sh
cp -v test-runs_template.sh ./test-runs584.sh
cp -v para-runs_template.sh ./para-runs584.sh

sed -i 's/xxx/584/g' launch-runs584.sh
sed -i 's/xxx/584/g' test-runs584.sh
sed -i 's/xxx/584/g' para-runs584.sh

chmod 777 ./launch-runs584.sh
chmod 777 ./test-runs584.sh
chmod 777 ./para-runs584.sh

cp -v launch-runs584.sh ../Runs/
cp -v test-runs584.sh ../Runs/
cp -v para-runs584.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_584.params ../paramsFiles

cd ../Runs

sh ./launch-runs584.sh > ../out584.out & bg
###############################################################


