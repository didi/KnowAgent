#!/bin/bash

function Printlog(){
	message=$1
	time=`date`
	echo "[ $time ] [info] $message" >> /var/log/know-agent.log
	echo "[ $time ] [info] $message"
}

function Install_Mysql(){
	wget -O "/tmp/mysql5.7.tar.gz" https://s3-gzpu.didistatic.com/pub/mysql5.7.tar.gz
	#卸载已安装的数据库，避免无法安装
	rpm -qa | grep -E "mariadb|mysql" | xargs yum -y remove 2> /dev/null
    #备份历史数据（如有）
	mv -f /var/lib/mysql/ /var/lib/mysqlbak$(date "+%s")
	tar -zxf /tmp/mysql5.7.tar.gz -C /tmp/
	yum -y localinstall /tmp/mysql* /tmp/libaio*
	systemctl start mysqld
	if [ $? -eq 0 ];then
		Printlog "MySQL installation completed～"
		systemctl enable mysqld 1>/dev/null 2>&1
		old_pwd=`grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -n 1`
		echo "$mysql_pwd" > mysql.password
		mysql -NBe "alter user USER() identified by '$mysql_pwd';" --connect-expired-password -uroot -p$old_pwd
	else
		Printlog "MySQL installation failed"
		exit
	fi

}

function Install_Java(){
	ls -l  /usr/local/java11 2> /dev/null
	if [ $? -eq 0 ];then
		echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
    	echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
    	echo "export PATH=\$JAVA_HOME/bin:\$PATH:\$HOME/bin" >> ~/.bashrc
    	source ~/.bashrc
    else
    	wget -O "/tmp/jdk11.tar.gz" https://s3-gzpu.didistatic.com/pub/jdk11.tar.gz
    	tar -zxf /tmp/jdk11.tar.gz -C /usr/local/
    	mv -f /usr/local/jdk-11.0.2 /usr/local/java11
    	echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
    	echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
    	echo "export PATH=\$JAVA_HOME/bin:\$PATH:\$HOME/bin" >> ~/.bashrc
    	source ~/.bashrc
    fi
}

function Start_Kafka_ZK(){
	wget -O "/tmp/kafka_${kafka_version}.tgz" https://s3-gzpu.didistatic.com/knowagent/kafka_""${kafka_version}"".tgz
	tar -zxf /tmp/kafka_"${kafka_version}".tgz -C $Dir/
	nohup sh $Dir/kafka_"${kafka_version}"/bin/zookeeper-server-start.sh $Dir/kafka_"${kafka_version}"/config/zookeeper.properties &
	sleep 3
	nohup sh $Dir/kafka_"${kafka_version}"/bin/kafka-server-start.sh $Dir/kafka_"${kafka_version}"/config/server.properties &
	sleep 5
	sh $Dir/kafka_"${kafka_version}"/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic metrics
	sh $Dir/kafka_"${kafka_version}"/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic errorlogs
	sh $Dir/kafka_"${kafka_version}"/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic data
}

function Start_LogPrint(){
	wget -O "/tmp/logprint_${logprint_version}.tar.gz" https://s3-gzpu.didistatic.com/knowagent/logprint_""${logprint_version}"".tar.gz
	tar -zxf /tmp/logprint_"${logprint_version}".tar.gz -C $Dir/
	cd $Dir/logprint_"${logprint_version}"
	sh start.sh

}


function Start_agent_manager(){
	wget -O "/tmp/agent-manager_${agent_manager_version}.tar.gz" https://s3-gzpu.didistatic.com/knowagent/agent-manager_""${agent_manager_version}"".tar.gz
	tar -zxf /tmp/agent-manager_"${agent_manager_version}".tar.gz -C $Dir/
	if [ "$mysql_ip" == "" ];then
		sed -i "s/mysql_pwd/${mysql_pwd}/g" $Dir/agent-manager_"${agent_manager_version}"/application.yml
		mysql -uroot -p"$mysql_pwd" < $Dir/agent-manager_"${agent_manager_version}"/create_mysql_table.sql
		cd $Dir/agent-manager_"${agent_manager_version}"/
		sh start.sh
	else
		sed -i "s/127.0.0.1/${mysql_ip}/g" $Dir/agent-manager_"${agent_manager_version}"/application.yml
		sed -i "s/mysql_pwd/${mysql_pwd}/g" $Dir/agent-manager_"${agent_manager_version}"/application.yml

		mysql -h"$mysql_ip" -uroot -p"$mysql_pwd"  "$dbname" < $Dir/agent-manager_"${agent_manager_version}"/create_mysql_table.sql
		cd $Dir/agent-manager_"${agent_manager_version}"/
		sh start.sh
	fi
}

function Start_agent(){
	wget -O "/tmp/agent_${agent_version}.tar.gz" https://s3-gzpu.didistatic.com/knowagent/agent_""${agent_version}"".tar.gz
	tar -zxf /tmp/agent_${agent_version}.tar.gz -C $Dir/
	cd $Dir/agent_"${agent_version}"
	sh start.sh
}



while :
do
	read -p "Do you need to install MySQL（yes/no）： " my_result
	if [ "$my_result" == "no" ];then
		read -p "Please enter the MySQL service address：" mysql_ip
		read -p "Please enter the root password of MySQL service：" mysql_pwd
		break
	elif [ "$my_result" == "yes" ];then
		mysql_pwd=`date +%s |sha256sum |base64 |head -c 10 ;echo`"_Di2"
		Install_Mysql
		break
	else
		Printlog "Input error, please re-enter（yes/no）"
		continue
	fi
done

Dir=`pwd`
#可选2.13-2.7.0
kafka_version="2.13-2.7.0"
#可选1.0
agent_manager_version="1.0"
#可选1.0
agent_version="1.0"
#可选1.0
logprint_version="1.0"


Install_Java
Start_Kafka_ZK
Start_LogPrint
Start_agent_manager
Start_agent


