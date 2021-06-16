FROM hub.xiaojukeji.com/alphacloud/elastic/ac_paas_base:stable

MAINTAINER lisuritu <lisuritu@didiglobal.com>

ENV GC_STRATEGY="-XX:+UseParallelGC -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/workspace/logs/heapdump.hprof -Xss2048k -Xmx6G -Xms6G"

RUN mkdir -p /workspace
ADD ./start.sh /workspace/start.sh
ADD ./webterminal.sh /workspace/webterminal.sh
COPY ./start.conf /etc/supervisord.d/

ENV ES_INDEX=oe-public-demo-java-demo
ENV BEAT_NAME=oe-public-demo-java-demo

ADD *.jar /workspace/

EXPOSE 22 8006