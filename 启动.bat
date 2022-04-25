call mvn clean package -Pbot
copy .\target\qqbot.jar qqbot.jar
CHCP 65001
call java -Djava.ext.dirs=./target/lib -Dfile.encoding=utf-8 -jar qqbot.jar
call pause