#!/bin/sh
ip a |awk '/state UP/{print $2}' | sed 's/://' | while read output;
do
  echo $output:
  ethtool -P $output
done