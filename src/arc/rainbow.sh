CP=`echo rainbow/java-lib/* | sed 's/ /\:/g'`
java -server -cp rainbow.jar:$CP rainbow.Console  $*
