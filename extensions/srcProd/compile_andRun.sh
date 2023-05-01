#! /bin/bash
echo ------ compiling ----------------------

cd ../

~/jdk-11.0.11/bin/javac -cp "./allJar/*" -Xmaxerrs 5 -d .  *.java

cd extensions

~/jdk-11.0.11/bin/javac -cp "../allJar/*" -Xmaxerrs 5 -d .  *.java

echo ...

echo ------ end compilation ----------------
echo 

~/jdk-11.0.11/bin/java -cp "./;../allJar/*" extensions.Extensions extensionsOutcomes_R01.params

