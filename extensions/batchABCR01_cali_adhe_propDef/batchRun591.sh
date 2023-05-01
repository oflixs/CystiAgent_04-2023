#! /bin/bash

###############################################################
#Village 591

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs591.sh
cp -v test-runs_template.sh ./test-runs591.sh
cp -v para-runs_template.sh ./para-runs591.sh

sed -i 's/xxx/591/g' launch-runs591.sh
sed -i 's/xxx/591/g' test-runs591.sh
sed -i 's/xxx/591/g' para-runs591.sh

chmod 777 ./launch-runs591.sh
chmod 777 ./test-runs591.sh
chmod 777 ./para-runs591.sh

cp -v launch-runs591.sh ../Runs/
cp -v test-runs591.sh ../Runs/
cp -v para-runs591.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_591.params ../paramsFiles

cd ../Runs

sh ./launch-runs591.sh > ../out591.out & bg
###############################################################


