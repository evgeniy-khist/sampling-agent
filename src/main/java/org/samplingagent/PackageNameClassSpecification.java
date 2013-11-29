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

import java.util.List;

/**
 *
 * @author Evgeniy Khist
 */
public class PackageNameClassSpecification implements ClassSpecification {

    private final boolean includeOrExclude;
    private final List<String> packages;

    private PackageNameClassSpecification(boolean includeOrExclude, List<String> packages) {
        this.includeOrExclude = includeOrExclude;
        this.packages = packages;
    }

    public static PackageNameClassSpecification include(List<String> packages) {
        return new PackageNameClassSpecification(true, packages);
    }

    public static PackageNameClassSpecification exclude(List<String> packages) {
        return new PackageNameClassSpecification(false, packages);
    }

    @Override
    public boolean isSatisfiedBy(String className) {
        for (String packageName : packages) {
            if (className.startsWith(packageName)) {
                return includeOrExclude;
            }
        }
        return !includeOrExclude;
    }
}
