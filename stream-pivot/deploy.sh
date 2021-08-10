#!/bin/bash

username=dc2-user
host_list=$@
echo "work dir: `pwd`"
#tar -czf agent.tgz log-agent/output/

set -x

for host in ${host_list} ; do
	scp target/stream-pivot-1.0-SNAPSHOT.jar ${username}@${host}:/home/${username}/work
done
