#!/bin/bash

if [ "$1" == "" ]; then
  echo "Usage: ./install.sh <arc_home>"
  echo 
  echo "Where <arc_home> is the path to your arc install (anarki or arcN)"
  echo "This will copy rainbow.jar and some supporting arc files to your arc install"
  echo "Note that some rainbow features require anarki"
  echo
  exit
fi

echo "installing rainbow in $1"
echo "WARNING: this adds files to your copy of arc. Be careful when pushing back to the repository if you are using version control"

cp ./rainbow.jar $1
cp lib $1/lib

if [ "$?" = "0" ]; then
  echo "Copy successful. Now cd $1 and run java -jar rainbow.jar"
else
  echo "Copy failed."
fi

