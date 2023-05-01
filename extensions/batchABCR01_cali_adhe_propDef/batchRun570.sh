#! /bin/bash

###############################################################
#Village 570

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs570.sh
cp -v test-runs_template.sh ./test-runs570.sh
cp -v para-runs_template.sh ./para-runs570.sh

sed -i 's/xxx/570/g' launch-runs570.sh
sed -i 's/xxx/570/g' test-runs570.sh
sed -i 's/xxx/570/g' para-runs570.sh

chmod 777 ./launch-runs570.sh
chmod 777 ./test-runs570.sh
chmod 777 ./para-runs570.sh

cp -v launch-runs570.sh ../Runs/
cp -v test-runs570.sh ../Runs/
cp -v para-runs570.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_570.params ../paramsFiles

cd ../Runs

sh ./launch-runs570.sh > ../out570.out & bg
###############################################################


