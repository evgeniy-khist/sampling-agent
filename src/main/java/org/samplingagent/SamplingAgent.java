/*
 * Copyright 2013 Evgeniy Khist
 *
 * Licensed under the Apache License, ArticleVersion 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.samplingagent;

import java.lang.instrument.Instrumentation;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Arrays.asList;

/**
 *
 * @author Evgeniy Khist
 */
public class SamplingAgent {
    
    private static final Logger logger = Logger.getLogger(SamplingAgent.class.getName());
    
    public static void premain(String args, Instrumentation instrumentation) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        int samplingInterval = 100;
        int outputInterval = 20000;
        String outputWriterClassName = LoggerOutputWriter.class.getName();
        
        for (String keyValueStr : args.split(",")) {
            String[] keyValue = keyValueStr.split("=");
            switch (keyValue[0]) {
                case "samplingInterval":
                    samplingInterval = Integer.parseInt(keyValue[1]);
                    break;
                case "outputInterval":
                    outputInterval = Integer.parseInt(keyValue[1]);
                    break;
                case "outputWriter":
                    outputWriterClassName = keyValue[1];
                    break;
            }
        }
        
        logger.log(Level.INFO, 
                String.format("SamplingAgent started with samplingInterval=%s", samplingInterval));
        
        String includePackages = System.getProperty("org.samplingagent.includePackages");
        String excludePackages = System.getProperty("org.samplingagent.excludePackages");
        
        if (includePackages != null && excludePackages != null) {
            throw new RuntimeException("Both includePackages and excludePackages can't be specified at the same time");
        }
        
        ClassSpecification classSpecification;
        if (includePackages != null) {
            logger.log(Level.INFO, 
                String.format("Only classes from packages %s will be sampled", includePackages));
            classSpecification = PackageNameClassSpecification.include(asList(includePackages.split(",")));
        } else if (excludePackages != null) {
            logger.log(Level.INFO, 
                String.format("Classes from packages %s will not be sampled", excludePackages));
            classSpecification = PackageNameClassSpecification.exclude(asList(excludePackages.split(",")));
        } else {
            classSpecification = new NoopClassSpecification();
        }
        
        SamplingTask samplingTask = new SamplingTask(samplingInterval, classSpecification);
        
        Thread samplingThread = new Thread(samplingTask);
        samplingThread.setDaemon(true);
        samplingThread.setName("samplingagent-sampling-thread");
        samplingThread.start();
        
        OutputWriter outputWriter = (OutputWriter) Class.forName(outputWriterClassName).newInstance();
        
        OutputTask outputTask = new OutputTask(outputInterval, samplingTask, outputWriter);
        Thread outputThread = new Thread(outputTask);
        outputThread.setDaemon(true);
        samplingThread.setName("samplingagent-output-thread");
        outputThread.start();
    }
}
