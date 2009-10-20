CP=`echo rainbow/java-lib/* | sed 's/ /\:/g'`
java -Dswing.aatext=true -server -cp rainbow.jar:$CP rainbow.Console  $*

