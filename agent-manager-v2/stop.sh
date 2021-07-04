#!/bin/bash

if [[ -f am.pid ]]; then
    kill -9 `cat am.pid`
    echo 'service stopped'
else
    echo 'am.pid file not found'
    exit 1
fi
