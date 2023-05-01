#! /bin/bash

###############################################################
#Village 574

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs574.sh
cp -v test-runs_template.sh ./test-runs574.sh
cp -v para-runs_template.sh ./para-runs574.sh

sed -i 's/xxx/574/g' launch-runs574.sh
sed -i 's/xxx/574/g' test-runs574.sh
sed -i 's/xxx/574/g' para-runs574.sh

chmod 777 ./launch-runs574.sh
chmod 777 ./test-runs574.sh
chmod 777 ./para-runs574.sh

cp -v launch-runs574.sh ../Runs/
cp -v test-runs574.sh ../Runs/
cp -v para-runs574.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_574.params ../paramsFiles

cd ../Runs

sh ./launch-runs574.sh > ../out574.out & bg
###############################################################


