CP=`echo rainbow/java-lib/* | sed 's/ /\:/g'`
java -Xmx1G -Dswing.aatext=true -server -cp rainbow.jar:$CP rainbow.Console  $*

