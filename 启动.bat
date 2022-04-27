call mvn clean package -Pdw
copy .\target\lasbot.jar lasbot.jar
CHCP 65001
call java -Djava.ext.dirs=./target/lib -Dfile.encoding=utf-8 -jar lasbot.jar
call pause