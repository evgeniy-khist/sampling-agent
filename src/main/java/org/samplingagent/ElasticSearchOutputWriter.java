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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.samplingagent.elasticsearch.util.IndexNameBuilder;
import static org.samplingagent.util.ConfigurationUtils.getInt;
import static org.samplingagent.util.ConfigurationUtils.getString;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 *
 * @author Evgeniy Khist
 */
public class ElasticSearchOutputWriter implements OutputWriter {

    private static final Logger logger = Logger.getLogger(ElasticSearchOutputWriter.class.getName());
    
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    
    private static final String ELASTICSEARCH_HOST = "org.samplingagent.elasticsearchHost";
    private static final String ELASTICSEARCH_HOST_DEFAULT_VALUE = "localhost";
    
    private static final String ELASTICSEARCH_PORT = "org.samplingagent.elasticsearchPort";
    private static final int ELASTICSEARCH_PORT_DEFAULT_VALUE = 9300;
    
    private static final String ELASTICSEARCH_CLUSTER_NAME = "org.samplingagent.elasticsearchClusterName";
    private static final String ELASTICSEARCH_CLUSTER_NAME_DEFAULT_VALUE = "elasticsearch";
    
    private static final String ELASTICSEARCH_INDEX = "org.samplingagent.elasticsearchIndex";
    private static final String ELASTICSEARCH_INDEX_DEFAULT_VALUE = "sampling-%{yyyy.MM.dd}";
    
    private static final String TYPE_DEFAULT_VALUE = "sampling";
    
    private static final String NODE_NAME = "org.samplingagent.nodeName";
    
    private Client elasticsearchClient;
    private IndexNameBuilder indexNameBuilder;
    
    private String host;
    private String nodeName;

    public ElasticSearchOutputWriter() {
        Properties settings = System.getProperties();

        String elasticSearchHost = getString(settings, ELASTICSEARCH_HOST, ELASTICSEARCH_HOST_DEFAULT_VALUE);
        int elasticSearchPort = getInt(settings, ELASTICSEARCH_PORT, ELASTICSEARCH_PORT_DEFAULT_VALUE);
        String elasticsearchClusterName = getString(settings, ELASTICSEARCH_CLUSTER_NAME, ELASTICSEARCH_CLUSTER_NAME_DEFAULT_VALUE);

        Settings elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", elasticsearchClusterName)
                .put("client.transport.sniff", true)
                .build();

        elasticsearchClient = new TransportClient(elasticsearchSettings)
                .addTransportAddress(new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));

        logger.log(Level.INFO,
                String.format("ElasticSearchOutputWriter is configured with host=%s, port=%s", elasticSearchHost, elasticSearchPort));

        String indexNamePattern = getString(settings, ELASTICSEARCH_INDEX, ELASTICSEARCH_INDEX_DEFAULT_VALUE);
        indexNameBuilder = new IndexNameBuilder(indexNamePattern);

        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        nodeName = getString(settings, NODE_NAME, null);
    }

    @Override
    public void writeExecutionTime(String longMethodName, long totalExecutionTime) throws IOException {
        Date timestamp = new Date();
        String[] classNameAndMethodName = longMethodName.split("#");
        String className = classNameAndMethodName[0];
        String methodName = classNameAndMethodName[1];
        
        IndexResponse response = elasticsearchClient.prepareIndex(indexNameBuilder.build(timestamp), TYPE_DEFAULT_VALUE)
                .setSource(jsonBuilder()
                    .startObject()
                        .field("@timestamp", new SimpleDateFormat(TIMESTAMP_FORMAT).format(timestamp))
                        .field("host", host)
                        .field("nodeName", nodeName)
                        .field("className", className)
                        .field("methodName", methodName)
                        .field("totalExecutionTime", totalExecutionTime)
                    .endObject())
                .execute()
                .actionGet();
        
        logger.log(Level.INFO, 
                    String.format("Sampling results [%s, %s] sent to ElasticSearch responseId=%s", longMethodName, totalExecutionTime, response.getId()));
    }
}
