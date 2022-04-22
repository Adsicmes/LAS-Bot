call mvn clean package -Pbot
copy .\target\qqbot.jar qqbot.jar
call java -Djava.ext.dirs=./target/lib -jar qqbot.jar
call pause