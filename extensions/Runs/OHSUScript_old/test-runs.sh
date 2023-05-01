#!/bin/bash

#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=7
#SBATCH --mem-per-cpu=4300 # here OHSU: for 7 TTEMP village maxmemory  MaxRSS: 21.490 GB after 12 hours
#SBATCH --time=1-00:00 #time format : days-hours:minutes

 ./para-runs.sh 
