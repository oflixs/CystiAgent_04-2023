#! /bin/bash

###############################################################
#Village 590

#------- create ther launch test and para file
cp -v launch-runs_template.sh ./launch-runs590.sh
cp -v test-runs_template.sh ./test-runs590.sh
cp -v para-runs_template.sh ./para-runs590.sh

sed -i 's/xxx/590/g' launch-runs590.sh
sed -i 's/xxx/590/g' test-runs590.sh
sed -i 's/xxx/590/g' para-runs590.sh

chmod 777 ./launch-runs590.sh
chmod 777 ./test-runs590.sh
chmod 777 ./para-runs590.sh

cp -v launch-runs590.sh ../Runs/
cp -v test-runs590.sh ../Runs/
cp -v para-runs590.sh ../Runs/
#-------

cp  -v paramsFiles/extensionsABC_R01_adherence_propDef_590.params ../paramsFiles

cd ../Runs

sh ./launch-runs590.sh > ../out590.out & bg
###############################################################


