#! /bin/bash

###############################################################
#Village 563

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs563.sh
cp -v test-runs_template.sh ./test-runs563.sh
cp -v para-runs_template.sh ./para-runs563.sh

sed -i 's/xxx/563/g' launch-runs563.sh
sed -i 's/xxx/563/g' test-runs563.sh
sed -i 's/xxx/563/g' para-runs563.sh

chmod 777 ./launch-runs563.sh
chmod 777 ./test-runs563.sh
chmod 777 ./para-runs563.sh

cp -v launch-runs563.sh ../Runs/
cp -v test-runs563.sh ../Runs/
cp -v para-runs563.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_563.params ../paramsFiles

cd ../Runs

sh ./launch-runs563.sh > ../out563.out & bg
###############################################################


