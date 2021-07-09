#!/bin/bash
echo "shutting down!"
function get_pid() {
    local pid=`ps axu | grep "integration" | grep -v "old" |grep -v grep  | awk '{print $2}'`
    echo $pid
}

pid=$(get_pid)
if [ -z "$pid" ];then
    echo "process have been shutdown already"
else
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
