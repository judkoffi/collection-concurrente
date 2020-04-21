#!/bin/bash
set -ev

if [ "$FOLDER" = "lab5" ]; then
    if [ "$OSTYPE" = "darwin17" ]; then # OS = osx
        wget https://github.com/forax/java-next/releases/download/untagged-bf24edb7ff6b12ce0d49/jdk-15-vector-osx.tar.gz
        tar xvf jdk-15-vector-osx.tar.gz
    else
        wget https://github.com/forax/java-next/releases/download/untagged-bf24edb7ff6b12ce0d49/jdk-15-vector-linux.tar.gz
        tar xvf jdk-15-vector-linux.tar.gz
    fi
    export JAVA_HOME=$(pwd)/jdk-15-vector/
fi

cd $FOLDER

echo $JAVA_HOME

$JAVA_HOME/bin/java --version



mvn package

echo "--------------- Benchmark --------------------"
$JAVA_HOME/bin/java --add-modules jdk.incubator.vector -jar target/benchmarks.jar 
