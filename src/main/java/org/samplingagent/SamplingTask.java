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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Evgeniy Khist
 */
public class SamplingTask implements Runnable {

    private final int interval;
    private final ClassSpecification specification;

    private final Map<String, Long> sampling = new ConcurrentHashMap<>();
    private long lastSamplingTime;

    public SamplingTask(int interval, ClassSpecification specification) {
        this.interval = interval;
        this.specification = specification;
    }
    
    @Override
    public void run() {
        lastSamplingTime = System.currentTimeMillis();
        while (true) {
            sleep();
            
            Collection<StackTraceElement[]> allStackTraces = Thread.getAllStackTraces().values();
            long currentSamplingTime = System.currentTimeMillis();
            long deltaTime = currentSamplingTime - lastSamplingTime;
            
            for (StackTraceElement[] stackTrace : allStackTraces) {
                if (stackTrace.length == 0) {
                    continue;
                }
                
                for (StackTraceElement stackTraceElement : stackTrace) {
                    String className = stackTraceElement.getClassName();
                    if (specification.isSatisfiedBy(className)) {
                        String methodLongName = className + "#" + stackTraceElement.getMethodName();
                        Long executionTime = sampling.get(methodLongName);
                        sampling.put(methodLongName, executionTime != null ? executionTime += deltaTime : deltaTime);
                    }
                }
            }
            
            lastSamplingTime = currentSamplingTime;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Long> getSampling() {
        return Collections.unmodifiableMap(sampling);
    }
}
