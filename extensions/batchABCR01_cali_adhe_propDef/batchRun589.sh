#! /bin/bash

###############################################################
#Village 589

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs589.sh
cp -v test-runs_template.sh ./test-runs589.sh
cp -v para-runs_template.sh ./para-runs589.sh

sed -i 's/xxx/589/g' launch-runs589.sh
sed -i 's/xxx/589/g' test-runs589.sh
sed -i 's/xxx/589/g' para-runs589.sh

chmod 777 ./launch-runs589.sh
chmod 777 ./test-runs589.sh
chmod 777 ./para-runs589.sh

cp -v launch-runs589.sh ../Runs/
cp -v test-runs589.sh ../Runs/
cp -v para-runs589.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_589.params ../paramsFiles

cd ../Runs

sh ./launch-runs589.sh > ../out589.out & bg
###############################################################


