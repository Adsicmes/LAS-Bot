call mvn clean package -Pdw
copy .\target\qqbot.jar qqbot.jar
CHCP 65001
call java -Djava.ext.dirs=./target/lib -Dfile.encoding=utf-8 -jar qqbot.jar
call pause