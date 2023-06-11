#!/bin/bash

path=.

if [[ -n $1 ]]; then
    path=$1
fi

filePath=${path}/output.log

if [[ ! -f ${filePath} ]]; then
    echo 'file not exists'
    exit 1
fi

while [[ 1 ]]; do
    sleep 5
    head -n 50 ${path}/output.log >> ${path}/output.log
done
