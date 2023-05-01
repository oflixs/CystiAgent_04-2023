#! /bin/bash
echo ------ compiling ----------------------

~/jdk-11.0.11/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java

echo ...

echo ------ end compilation ----------------
echo 

