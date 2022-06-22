#!/bin/sh
set -x
function Printlog(){
	message=$1
	time=`date`
	echo "[ $time ] [info] $message" >> /var/log/logi-em.log
	echo "[ $time ] [info] $message"
}

function Down_Package(){
	wget -O "/root/LogiAM.tar.gz" https://wri.s3.didiyunapi.com/LogiAM.tar.gz
	if [ $? -eq 0 ];then
		tar -zxf /root/LogiAM.tar.gz -C /root/
	else
		Printlog "Package download failed, script exit"
		exit
	fi
}

function Install_Mysql(){
	wget -O "/root/mysql5.7.tar.gz" https://logi-em.s3.didiyunapi.com/mysql5.7.tar.gz
	#卸载已安装的数据库，避免无法安装
	rpm -qa | grep -E "mariadb|mysql" | xargs yum -y remove > /dev/null
    #备份历史数据（如有）
	mv -f /var/lib/mysql/ /var/lib/mysqlbak$(date "+%s")
	mkdir -p /root/mysql/
	tar -zxf /root/mysql5.7.tar.gz -C /root/mysql/
	rpm -ivh /root/mysql/mysql-community-common-5.7.36-1.el7.x86_64.rpm
	rpm -ivh /root/mysql/mysql-community-libs-5.7.36-1.el7.x86_64.rpm
	rpm -ivh /root/mysql/mysql-community-devel-5.7.36-1.el7.x86_64.rpm
	rpm -ivh /root/mysql/mysql-community-libs-compat-5.7.36-1.el7.x86_64.rpm
	rpm -ivh /root/mysql/mysql-community-client-5.7.36-1.el7.x86_64.rpm
	rpm -ivh /root/mysql/libaio-0.3.109-13.el7.x86_64.rpm 
	rpm -ivh /root/mysql/mysql-community-server-5.7.36-1.el7.x86_64.rpm
	systemctl start mysqld
	systemctl enable mysqld 1>/dev/null 2>&1
	old_pass=`grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -n 1`
	echo "$mysql_pass" > mysql.password
	mysql -NBe "alter user USER() identified by '$mysql_pass';" --connect-expired-password -uroot -p$old_pass
	if [ $? -eq 0 ];then
		Printlog "MySQL installation completed～"
	else
		Printlog "MySQL installation failed"
		exit
	fi
}


function Install_Java(){
    tar -zxf /root/jdk11.tar.gz -C /usr/local/
    mv -f /usr/local/jdk-11.0.2 /usr/local/java11
    echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
    echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
    echo "export PATH=\$JAVA_HOME/bin:\$PATH:\$HOME/bin" >> ~/.bashrc
    source ~/.bashrc
}

function Start_Kafka_ZK(){
	nohup sh $Dir/kafka_2.13-2.7.0/bin/kafka-server-start.sh $Dir/kafka_2.13-2.7.0/config/server.properties &
	nohup sh $Dir/kafka_2.13-2.7.0/bin/zookeeper-server-start.sh $Dir/kafka_2.13-2.7.0/config/zookeeper.properties &

}

function Start_agentd(){
	cd $Dir/logi_agent
	sh control start
}


function Start_AM_Manager(){
	if [ "$mysql_ip" == "" ];then
		sed -i "s/mysql_pass/${mysql_pass}/g" $Dir/logi_am/application.yml
		mysql -uroot -p"$mysql_pass" < $Dir/logi_am/create_mysql_table.sql
		cd $Dir/logi_am/
		sh start.sh
	else
		sed -i "s/127.0.0.1/${mysql_ip}/g" $Dir/logi_am/application.yml
		sed -i "s/mysql_pass/${mysql_pass}/g" $Dir/logi_am/application.yml
		mysql -h"$mysql_ip" -uroot -p"$mysql_pass" < $Dir/logi_am/create_mysql_table.sql
		cd $Dir/logi_am/
		sh start.sh
	fi
}

function Start_LogPrint(){
	cd $Dir/logprint/
	sh start.sh

}


while : 
do
	read -p "Do you need to install MySQL（yes/no）： " my_result
	if [ "$my_result" == "no" ];then
		read -p "Please enter the MySQL service address：" mysql_ip
		read -p "Please enter the root password of MySQL service：" mysql_pass
		break
	elif [ "$my_result" == "yes" ];then
		mysql_pass=`date +%s |sha256sum |base64 |head -c 10 ;echo`"_Di2"
		Install_Mysql
		break
	else
		Printlog "Input error, please re-enter（yes/no）"
		continue
	fi
done
Dir=`pwd`
Down_Package
Install_Java
Start_Kafka_ZK
Start_agentd
Start_AM_Manager
Start_LogPrint
