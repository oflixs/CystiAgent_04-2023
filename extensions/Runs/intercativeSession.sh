srun --job-name "InteractiveJob" --nodes=1 --mem-per-cpu=1500 --ntasks=1 --cpus-per-task=12 --time=1-00:00 ./para-runs.sh  -p light --pty bash
salloc --job-name "InteractiveJob" --nodes=1 --mem-per-cpu=1500 --ntasks=1 --cpus-per-task=12 --time=1-00:00 -p light 
