sampling-agent
==============
```
JAVA_OPTS="$JAVA_OPTS -javaagent:sampling-agent.jar=samplingInterval=100,outputInterval=20000,outputWriter=org.samplingagent.ElasticSearchOutputWriter"
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.includePackages=com.example." # empty by default
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.excludePackages=java.,javax.,com.sun." # empty by default
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.elasticsearchHost=localhost -Dorg.samplingagent.elasticsearchPort=9300 -Dorg.samplingagent.elasticsearchClusterName=elasticsearch -Dorg.samplingagent.elasticsearchIndex=sampling-%{yyyy.MM.dd}"
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.nodeName=nodeName" # empty by default
```