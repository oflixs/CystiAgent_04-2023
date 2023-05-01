#! /bin/bash

###############################################################
#Village 571

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs571.sh
cp -v test-runs_template.sh ./test-runs571.sh
cp -v para-runs_template.sh ./para-runs571.sh

sed -i 's/xxx/571/g' launch-runs571.sh
sed -i 's/xxx/571/g' test-runs571.sh
sed -i 's/xxx/571/g' para-runs571.sh

chmod 777 ./launch-runs571.sh
chmod 777 ./test-runs571.sh
chmod 777 ./para-runs571.sh

cp -v launch-runs571.sh ../Runs/
cp -v test-runs571.sh ../Runs/
cp -v para-runs571.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_571.params ../paramsFiles

cd ../Runs

sh ./launch-runs571.sh > ../out571.out & bg
###############################################################


