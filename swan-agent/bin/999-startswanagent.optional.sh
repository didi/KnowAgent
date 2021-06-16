#!/bin/bash
app="swan-agent"
appDir="/home/odin/super-agent/data/install/$app/current"
appVsnFile="$appDir/.deploy/agent_version"
privateDir="/home/odin/super-agent/data/install/private/$app"
runningDir="/home/odin/super-agent/data/install/running"
denyDir="/etc/odin-super-agent/"
denyFile="agents.deny"

if [ "x$DEPLOY_SWAN_NOT_NEED_START" == "x1" ];then
    echo "not need start $app agent"
    exit 0
fi

lt1=$(date +%s)
mkdir -p $privateDir && cd $privateDir && mkdir data var log &>/dev/null
mkdir -p $runningDir && cd $runningDir && ln -s $appDir $app
cd $appDir && bash control install >> /home/odin/super-agent/data/install/private/$app/log/start.log 2>&1  && bash control start >> /home/odin/super-agent/data/install/private/$app/log/start.log 2>&1
chown -R logger:logger /home/xiaoju/meta/swan
lt2=$(date +%s)
deta=$(($lt2-$lt1))
echo "start agent $app in $deta seconds, version $(cat $appVsnFile)"
exit 0

