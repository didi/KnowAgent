#!/bin/bash

mvn clean install -Dmaven.test.skip=true

ret=$?
if [ $ret -ne 0 ];then
    echo "===== maven build failed! ====="
    exit $ret
else
    echo -n "===== maven build succeeded! ====="
fi

rm -rf output
mkdir output
# 拷贝Dockerfile及其需要的文件至output目录下
cp start.sh output/
cp stop.sh output/
cp agent-manager-rest/target/*.jar output/
cp agent-manager-rest/src/main/resources/application.yml output
