sampling-agent
==============
```
JAVA_OPTS="$JAVA_OPTS \
-javaagent:sampling-agent.jar=samplingInterval=100,outputInterval=20000,outputWriter=org.samplingagent.ElasticSearchOutputWriter \
-Dorg.samplingagent.includePackages=com.example. \
-Dorg.samplingagent.excludePackages=java.,javax.,com.sun. \
-Dorg.samplingagent.elasticsearchHost=localhost \
-Dorg.samplingagent.elasticsearchPort=9300 \ 
-Dorg.samplingagent.elasticsearchClusterName=elasticsearch \
-Dorg.samplingagent.elasticsearchIndex=sampling-%{yyyy.MM.dd} \
-Dorg.samplingagent.nodeName=nodeName"
```

`org.samplingagent.includePackages` empty by default
`org.samplingagent.excludePackages` empty by default
`org.samplingagent.nodeName=nodeName` empty by default
