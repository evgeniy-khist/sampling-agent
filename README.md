sampling-agent
==============
**sampling-agent** is started by adding `javaagent` option to the command-line. Configuration can be done with javaagent aptions and system properties.

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

`samplingInterval`, `outputInterval` and `outputWriter` are mandatory options.

`samplingInterval` and `outputInterval` specify time in milliseconds. 

`org.samplingagent.includePackages`, `org.samplingagent.excludePackages` and `org.samplingagent.nodeName` are empty by default

`org.samplingagent.includePackages` and `org.samplingagent.excludePackages` shouldn't be used at the same time.

[LoggerOutputWriter](https://github.com/evgeniy-khist/sampling-agent/blob/master/src/main/java/org/samplingagent/LoggerOutputWriter.java) logs sampling data with *INFO* level using Java Util Logging. 

[ElasticSearchOutputWriter](https://github.com/evgeniy-khist/sampling-agent/blob/master/src/main/java/org/samplingagent/ElasticSearchOutputWriter.java) sends sampling data to Elasticsearch.

`org.samplingagent.elasticsearchHost`, `org.samplingagent.elasticsearchPort`, `org.samplingagent.elasticsearchClusterName`, `org.samplingagent.elasticsearchIndex` properties are used for configuring *ElasticSearchOutputWriter*.

If value of `org.samplingagent.elasticsearchIndex` contains date placeholder like `%{yyyy.MM.dd}` it will be replaced with current date and time string representation based on specified format.
