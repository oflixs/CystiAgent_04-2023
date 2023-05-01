#!/bin/bash

#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=10
#SBATCH --mem-per-cpu=64000 # 2500MB to simulate 3 TTEMP villages 5500MB to sinmulate 8 TTEMP villages 12000MB to simulate 10 R01 villages
#SBATCH --time=1-00:00 #time format : days-hours:minutes

 ./para-runs.sh 
