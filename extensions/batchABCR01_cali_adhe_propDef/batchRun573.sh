#! /bin/bash

###############################################################
#Village 573

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs573.sh
cp -v test-runs_template.sh ./test-runs573.sh
cp -v para-runs_template.sh ./para-runs573.sh

sed -i 's/xxx/573/g' launch-runs573.sh
sed -i 's/xxx/573/g' test-runs573.sh
sed -i 's/xxx/573/g' para-runs573.sh

chmod 777 ./launch-runs573.sh
chmod 777 ./test-runs573.sh
chmod 777 ./para-runs573.sh

cp -v launch-runs573.sh ../Runs/
cp -v test-runs573.sh ../Runs/
cp -v para-runs573.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_573.params ../paramsFiles

cd ../Runs

sh ./launch-runs573.sh > ../out573.out & bg
###############################################################


