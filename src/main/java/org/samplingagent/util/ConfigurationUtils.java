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
package org.samplingagent.util;

import java.util.Properties;

/**
 *
 * @author Evgeniy Khist
 */
public class ConfigurationUtils {

    public static String getString(Properties settings, String name, String defaultValue) {
        return settings.containsKey(name) ? String.valueOf(settings.get(name)) : defaultValue;
    }
    
    public static int getInt(Properties settings, String name, int defaultValue) {
        return settings.containsKey(name) ? Integer.parseInt(String.valueOf(settings.get(name))) : defaultValue;
    }
}
