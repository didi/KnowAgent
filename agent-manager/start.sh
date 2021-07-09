#!/bin/bash

if [[ -f am.pid ]]; then
    kill -9 `cat am.pid`
fi
JAR_FILE=`ls | grep .jar\$`

GC_STRATEGY="-XX:+UseParallelGC -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/heapdump.hprof -Xss2048k -Xmx2G -Xms2G"

nohup java ${GC_STRATEGY} -jar ${JAR_FILE} &

echo $! > am.pid
echo "service started as pid: $!"
