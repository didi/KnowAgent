#!/bin/bash
workspace=$(cd $(dirname $0) && pwd -P)
cd $workspace

## const
app=log-collector

gitversion=.gitversion
## function
function build() {
    # 进行编译
    # cmd
    #mvn clean install
    
    JVERSION=`java -version 2>&1 | awk 'NR==1{gsub(/"/,"");print $3}'`
    major=`echo $JVERSION | awk -F. '{print $1}'`
    mijor=`echo $JVERSION | awk -F. '{print $2}'`
    if [ $major -le 1 ] && [ $mijor -lt 8 ]; then
        export JAVA_HOME=/usr/local/jdk1.8.0_65  #(使用jdk8请设置)
        export PATH=$JAVA_HOME/bin:$PATH
    fi
    mvn clean install -P online -Dmaven.test.skip

    local sc=$?
    if [ $sc -ne 0 ];then
        ## 编译失败, 退出码为 非0
        echo "$app build error"
        exit $sc
    else
        echo "$app build ok, vsn="`gitversion`
    fi
}

function make_output() {
    # 新建output目录
    local output="./output"
    rm -rf $output &>/dev/null
    mkdir -p $output &>/dev/null
    mkdir -p $output/properties &>/dev/null
    mkdir -p $output/ddcloud/init &>/dev/null
    # 填充output目录, output内的内容 即为 线上部署内容
    (
        cp -rf bin/* $output &&         # 拷贝至output目录
        tar -zxf agent-node/target/${app}.tar.gz -C ${output} &&     # 解压war包到output目录
        cp -rf properties/*.properties ${output}/properties &&
        echo -e "make output ok."
    ) || { echo -e "make output failed!"; exit 2; } # 填充output目录失败后, 退出码为 非0
}

## internals
function gitversion() {
    git log -1 --pretty=%h > $gitversion
    local gv=`cat $gitversion`
    echo "$gv"
}


##########################################
## main
## 其中,
##        1.进行编译
##        2.生成部署包output
##########################################

# 1.进行编译
build

# 2.生成部署包output
make_output

# 编译成功
echo -e "build done."
exit 0
