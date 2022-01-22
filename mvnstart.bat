call mvn clean package -Pdw
call cd target
call java -Djava.ext.dirs=./lib -jar qqbot.jar
call pause