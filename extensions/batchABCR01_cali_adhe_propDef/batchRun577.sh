#! /bin/bash

###############################################################
#Village 577

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs577.sh
cp -v test-runs_template.sh ./test-runs577.sh
cp -v para-runs_template.sh ./para-runs577.sh

sed -i 's/xxx/577/g' launch-runs577.sh
sed -i 's/xxx/577/g' test-runs577.sh
sed -i 's/xxx/577/g' para-runs577.sh

chmod 777 ./launch-runs577.sh
chmod 777 ./test-runs577.sh
chmod 777 ./para-runs577.sh

cp -v launch-runs577.sh ../Runs/
cp -v test-runs577.sh ../Runs/
cp -v para-runs577.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_577.params ../paramsFiles

cd ../Runs

sh ./launch-runs577.sh > ../out577.out & bg
###############################################################


