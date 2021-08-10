#!/bin/bash

set -x

host=$1

if [ -z host ]; then
    echo "$0 hostname"
    exit 1
fi

workspace=/root/work
agent=agent-test.tgz

ssh root@${host} "mkdir work"

scp ./deploy.sh root@${host}:${workspace}
scp ./target/stream-pivot-1.0-SNAPSHOT.jar root@${host}:${workspace}
scp ./rate.properties root@${host}:${workspace}

scp ./${agent} root@${host}:/root

ssh root@${host} "tar -xzf ${agent}; cd output; ./control start"
#ssh root@${host} "cd ${workspace}; bash ./deploy.sh"
