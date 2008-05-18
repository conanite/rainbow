#!/bin/bash
ant jar
if [ "$?" = "0" ]; then
  ARC_HOME=$1
  shift
  if [ "`dirname $0`" = "." ]; then
    BASE=`pwd`
  else
    BASE=`dirname $0`
  fi
  cd $ARC_HOME
  java -jar ${BASE}/build/dist/rainbow.jar $*
fi

