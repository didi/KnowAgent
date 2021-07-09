#!/bin/bash
SERVICE_PATH="/home/logger/log-collector"
mkdir -p ${SERVICE_PATH}/var
cd $SERVICE_PATH && bash control install && bash control start
