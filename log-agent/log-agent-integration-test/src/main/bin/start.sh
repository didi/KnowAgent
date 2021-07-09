#!/bin/bash
TASK_NAME=log-agent-integration-test
BASE_DEPLOY_HOME=$(cd $(dirname $0)/..;pwd)
BASE_APP_HOME=$BASE_DEPLOY_HOME/lib
STD_LOG="$BASE_DEPLOY_HOME/$TASK_NAME-out.log"
ERR_LOG="$BASE_DEPLOY_HOME/$TASK_NAME-err.log"
HOME_DIR=`cd ~;pwd`

BASE_LIB="$BASE_APP_HOME"
BASE_LIB_JARS=`ls $BASE_LIB|grep .jar|awk '{print "'$BASE_LIB'/"$0}'|tr "\n" ":"`

BASE_CLASS_PATH=$BASE_DEPLOY_HOME/classes:$BASE_DEPLOY_HOME/conf:$BASE_LIB_JARS

APP_OPTS="-server -Xms1024m -Xmx1024m -Xmn512m -Xss256k -XX:PermSize=96m -XX:MaxPermSize=96m -XX:SurvivorRatio=5 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=1 -XX:ParallelCMSThreads=5 -XX:ParallelGCThreads=5 -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=68 -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:../logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=../logs -XX:ErrorFile=../logs/hs_err_pid%p.log -Xdebug -Xrunjdwp:transport=dt_socket,suspend=n,server=y,address=8002"

DEBUG_PORT=60301
JAVA_DEBUG_OPT=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"
JAVA_OPTS="-DtaskName=$TASK_NAME -Dbaselog=$HOME_DIR $APP_OPTS"
JAVA_OPTS=" $JAVA_OPTS -cp $BASE_CLASS_PATH "
JAVA="$JAVA_HOME/bin/java $JAVA_OPTS"

function get_pid() {
    local pid=`ps axu | grep "integration" | grep -v grep  | awk '{print $2}'`
    echo $pid
}

# 先停止
pid=$(get_pid)
if [ ! -z "$pid" ];then
    echo "killing pid $pid";
    kill $pid
    COUNT=10
    while [ ! -z $pid ] && [ $COUNT -gt 0 ]; do
        sleep 1
        pid=$(get_pid)
        echo "killing...."
        let COUNT-=1
    done

    echo $pid
    if [ ! -z $pid ];then
        echo "killing -9 $pid"
        kill -9 $pid
    fi

    echo "process has been shutdown"
fi

sh /home/logger/log-collector/bin/old_stop.sh
nohup $JAVA com.didichuxing.datachannel.agent.integration.test.IntegrationTest 1>>$STD_LOG 2>>$ERR_LOG &
sleep 3


pid=$(get_pid)
if [ -z "$pid" ];then
    echo "start failed!"
    exit 1
else
    echo "start success!"
    rm -rf /home/logger/.logOffSet
    sh /home/logger/log-collector/bin/old_start.sh
    exit 0
fi
else
    echo "${TASK_NAME} started already!"
    exit 1;
fi
