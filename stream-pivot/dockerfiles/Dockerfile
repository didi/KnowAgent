FROM java:8

ENV GC_STRATEGY="-XX:+UseParallelGC -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/workspace/logs/heapdump.hprof -Xss2048k -Xmx6G -Xms6G"

RUN mkdir -p /workspace
ADD stream-pivot-1.0-SNAPSHOT.jar /workspace/
ADD rate.properties /workspace/

WORKDIR /workspace
CMD ["java", "-jar", "stream-pivot-1.0-SNAPSHOT.jar"]
