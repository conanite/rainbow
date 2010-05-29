CP=`echo rainbow/java-lib/* | sed 's/ /\:/g'`
java -Xmx1G -Xdock:name=Rainbow -Dswing.aatext=true -server -cp rainbow.jar:$CP rainbow.Console  $*

