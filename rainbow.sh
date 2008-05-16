#!/bin/bash
ant jar
if [ "$?" = "0" ]; then
        cd src/java/arc
        java -jar ../../../build/dist/rainbow.jar $*
fi

