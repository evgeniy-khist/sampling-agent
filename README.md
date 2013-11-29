sampling-agent
==============
JAVA_OPTS="$JAVA_OPTS -javaagent:sampling-agent.jar=samplingInterval=100,outputInterval=20000"
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.includePackages=com.example."
JAVA_OPTS="$JAVA_OPTS -Dorg.samplingagent.excludePackages=java.,javax.,com.sun."