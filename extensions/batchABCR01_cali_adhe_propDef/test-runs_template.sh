#!/bin/bash

#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=1
#SBATCH --mem-per-cpu=5000 # here OHSU: for 7 TTEMP village maxmemory  MaxRSS: 21.490 GB after 12 hours; 1 comm 5 GB
#SBATCH --time=1-00:00 #time format : days-hours:minutes

 ./para-runsxxx.sh 
