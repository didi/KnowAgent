#!/bin/bash

set -x

#----------------------------------------日志格式------------------------------------------------------#
function ECHO_LOG() {
    echo `date +%F%n%T` hostname:`hostname` $@
}

#----------------------------------------参数列表------------------------------------------------------#
P_OPERATION_TYPE=${1}           # 任务类型 0：安装 1：卸载 2：升级

P_PACKAGE_NAME=${2}             # 包名
P_PACKAGE_MD5=${3}              # 包MD5
P_PACKAGE_URL=${4}              # 包下载地址

# P_OPERATION_TYPE=1           # 任务类型 0：安装 1：卸载 2：升级

# P_PACKAGE_NAME="swan-log-collector"             # 包名
# P_PACKAGE_MD5="93b589240dbd77e7a424738c58d8fd3c"              # 包MD5
# P_PACKAGE_URL="http://img-ys011.didistatic.com/static/bigdata_kafka/do1_pGFCx7DwCIkU6l8T9TvP"              # 包下载地址

WORKSPACE_DIR=$(cd ~ && pwd)    # 工作目录, 设置不同的目录, 可以安装不同的LogAgent

OP_LOG_AGENT_NAME="op_logi_agent"
LOG_AGENT_NAME="logi-agent"


# 检查并准备安装包
function check_and_download_files() {
	if [ "${P_OPERATION_TYPE}" == "1" ];then
		ECHO_LOG "uninstall task, continue task"
		return 0
	fi

    if [ -z "${P_OPERATION_TYPE}" -o -z "${P_PACKAGE_NAME}" -o -z "${P_PACKAGE_MD5}" -o -z "${P_PACKAGE_URL}"  ]; then
        ECHO_LOG "exist illegal params, exit task"
        exit 1
    fi

    if [[ $(expr ${P_PACKAGE_NAME} : '.*\.tgz') ]]; then
        DIR_NAME=${P_PACKAGE_NAME%.tgz}
        ECHO_LOG "dir name: ${DIR_NAME}"
    elif [[ $(expr ${P_PACKAGE_NAME} : '.*\.tar\.gz') ]]; then
        DIR_NAME=${P_PACKAGE_NAME%.tar.gz}
        ECHO_LOG "dir name: ${DIR_NAME}"
    else
        ECHO_LOG "file must end with .tgz or .tar.gz"
        exit 1
    fi

    ECHO_LOG "init workspace dir"
    rm -rf ${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}

    mkdir -p ${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}
    if [ $? -ne 0 ];then
        ECHO_LOG "init workspace dir failed, exit task"
        exit 1
    fi

	# 下载并检查文件
    ECHO_LOG "start download file"
    wget ${P_PACKAGE_URL} -O ${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}/${P_PACKAGE_NAME}
    if [ $? -ne 0 ];then
        ECHO_LOG "download failed, exit task"
        exit 1
    fi

    md5_sum=`md5sum "${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}/${P_PACKAGE_NAME}" | awk -F " " '{print $1}'`
    if [ "$md5_sum" !=  "${P_PACKAGE_MD5}" ];then
        ECHO_LOG "download success but md5 illegal, exit task"
        exit 1
    fi

    tar -xzf "${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}/${P_PACKAGE_NAME}"  -C "${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}"
    if [ $? -ne 0 ];then
        ECHO_LOG "decompression failed, exit task"
        exit 1
    fi
}


function check_java() {
	if [ "${P_OPERATION_TYPE}" == "1" ];then
		ECHO_LOG "uninstall task, return"
		return 0
	fi

    # 检测java环境
    if [[ $(java -version; echo $?) -ne 0 ]]; then
        yum -y install java-11*
    fi
}


# 安装
function install() {
	uninstall

	# 删除旧目录
	rm -rf ${WORKSPACE_DIR}/${LOG_AGENT_NAME}

	# 新建目录
	mv ${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}/output ${WORKSPACE_DIR}/${LOG_AGENT_NAME}/

	# 启动
	sh ${WORKSPACE_DIR}/${LOG_AGENT_NAME}/control start
}


# 卸载
function uninstall() {
	if [ ! -d "${WORKSPACE_DIR}/${LOG_AGENT_NAME}" ]; then
        ECHO_LOG "without ${LOG_AGENT_NAME}, uninstall success"
        return 0
    fi

    sh ${WORKSPACE_DIR}/${LOG_AGENT_NAME}/control stop
}

# 升级
function upgrade() {
	uninstall

    if [[ -d "${WORKSPACE_DIR}/${DIR_NAME}_ROLLBACK_BACK" ]]; then
        rm -rf "${WORKSPACE_DIR}/${DIR_NAME}_ROLLBACK_BACK"
    fi

	# 移动旧目录
	mv ${WORKSPACE_DIR}/${LOG_AGENT_NAME} "${WORKSPACE_DIR}/${DIR_NAME}_ROLLBACK_BACK"

	# 新建目录
	mv ${WORKSPACE_DIR}/${OP_LOG_AGENT_NAME}/output ${WORKSPACE_DIR}/${LOG_AGENT_NAME}

	# 启动
	sh ${WORKSPACE_DIR}/${LOG_AGENT_NAME}/control start
}

function execute_task() {
	# 执行相关动作
	if [ "${P_OPERATION_TYPE}" == "0" ]; then
		install
	elif [ "${P_OPERATION_TYPE}" == "1" ]; then
		uninstall
	else
		upgrade
	fi

	if [ $? -ne 0 ]; then
        ECHO_LOG "execute task failed, exit task"
		exit 1
	fi
}

ECHO_LOG "start task..."
ECHO_LOG "params: $@"

check_and_download_files

check_java

execute_task

ECHO_LOG "exec success, return 0"

exit 0