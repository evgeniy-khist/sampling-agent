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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 *
 * @author Evgeniy Khist
 */
public class SamplingAgent {
    
    private static Logger logger = Logger.getLogger(SamplingAgent.class.getName());
    
    public static void premain(String args, Instrumentation instrumentation) {
        final int interval = 100;
        Thread samplingThread = new Thread(new Runnable() {

            private ConcurrentMap<String, Long> statistics = new ConcurrentHashMap<>();
            
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(interval);
                    } catch(InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    for (StackTraceElement[] stackTrace : Thread.getAllStackTraces().values()) {
                        if (stackTrace.length == 0) {
                            continue;
                        }
                        for (StackTraceElement stackTraceElement : stackTrace) {
                            String methodLongName = stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName();
                            Long executionTime = statistics.get(methodLongName);
                            statistics.put(methodLongName, executionTime != null ? executionTime += interval : interval); //TODO currentSample - lastSample instead of interval
                        }
                    }
                }
            }
        });
        samplingThread.setDaemon(true);
        samplingThread.start();
    }
}
