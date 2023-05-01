#! /bin/bash
echo ------ compiling ----------------------
echo ......
#~/jdk1.8.0_131/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java
/hpc/home/fp36/jdk-16.0.2/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java
echo ------ end compiling ------------------
