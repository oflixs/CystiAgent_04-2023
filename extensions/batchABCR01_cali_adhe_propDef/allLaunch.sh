#! /bin/bash

#------- create params files
cd paramsFiles

sh ./editFiles.sh
#-------

#------- remove runs outs
cd ../

rm -v ../Runs/Outs/*
#-------

sh ./batchRun563.sh > out563.out 
sh ./batchRun570.sh > out570.out 
sh ./batchRun571.sh > out571.out 
sh ./batchRun572.sh > out572.out 
sh ./batchRun573.sh > out573.out 
sh ./batchRun574.sh > out574.out 
sh ./batchRun575.sh > out575.out 
sh ./batchRun576.sh > out576.out 
sh ./batchRun577.sh > out577.out 
sh ./batchRun578.sh > out578.out 
sh ./batchRun579.sh > out579.out 
sh ./batchRun580.sh > out580.out 
sh ./batchRun581.sh > out581.out 
sh ./batchRun582.sh > out582.out 
sh ./batchRun583.sh > out583.out 
sh ./batchRun584.sh > out584.out 
sh ./batchRun585.sh > out585.out 
sh ./batchRun586.sh > out586.out 
sh ./batchRun587.sh > out587.out 
sh ./batchRun588.sh > out588.out 
sh ./batchRun589.sh > out589.out 
sh ./batchRun590.sh > out590.out 
sh ./batchRun591.sh > out591.out 

