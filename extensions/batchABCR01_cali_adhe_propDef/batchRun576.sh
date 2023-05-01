#! /bin/bash

###############################################################
#Village 576

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs576.sh
cp -v test-runs_template.sh ./test-runs576.sh
cp -v para-runs_template.sh ./para-runs576.sh

sed -i 's/xxx/576/g' launch-runs576.sh
sed -i 's/xxx/576/g' test-runs576.sh
sed -i 's/xxx/576/g' para-runs576.sh

chmod 777 ./launch-runs576.sh
chmod 777 ./test-runs576.sh
chmod 777 ./para-runs576.sh

cp -v launch-runs576.sh ../Runs/
cp -v test-runs576.sh ../Runs/
cp -v para-runs576.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_576.params ../paramsFiles

cd ../Runs

sh ./launch-runs576.sh > ../out576.out & bg
###############################################################


