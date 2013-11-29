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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Evgeniy Khist
 */
public class OutputTask implements Runnable {

    private static final Logger logger = Logger.getLogger(ElasticSearchOutputWriter.class.getName());
    
    private final int interval;
    private final SamplingTask samplingTask;
    private final OutputWriter outputWriter;

    public OutputTask(int interval, SamplingTask samplingTask, OutputWriter outputWriter) {
        this.interval = interval;
        this.samplingTask = samplingTask;
        this.outputWriter = outputWriter;
    }

    @Override
    public void run() {
        while (true) {
            sleep();
            
            for (Map.Entry<String, Long> entry : samplingTask.getSampling().entrySet()) {
                try {
                    outputWriter.writeExecutionTime(entry.getKey(), entry.getValue());
                } catch(Exception e) {
                    logger.log(Level.WARNING, "Ignoring exception invoking writeExecutionTime");
                }
            }
        }
    }

    public int getInterval() {
        return interval;
    }

    public SamplingTask getSamplingTask() {
        return samplingTask;
    }

    public OutputWriter getOutputWriter() {
        return outputWriter;
    }

    private void sleep() {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
