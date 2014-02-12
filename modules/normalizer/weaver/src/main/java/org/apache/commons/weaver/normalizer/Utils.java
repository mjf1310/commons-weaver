/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.weaver.normalizer;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Normalization utilities.
 */
final class Utils {
    private Utils() {
    }

    /**
     * Validate a package name.
     * @param pkg to validate
     * @return {@code pkg}, ({@code ""} if {@code null}), having replaced
     *         {@code '.'} with {@code '/'} and removed any terminating separator
     * @throws IllegalArgumentException if invalid
     */
    static String validatePackageName(String pkg) {
        if (StringUtils.isBlank(pkg)) {
            return "";
        }
        String result = pkg.trim();

        final String unexpected = "Unexpected character %s at pos %s of package name \"%s\"";

        boolean next = true;
        for (int pos = 0; pos < result.length(); pos++) {
            final char c = result.charAt(pos);
            if (next) {
                next = false;
                Validate.isTrue(Character.isJavaIdentifierStart(c), unexpected, c, pos, result);
                continue;
            }
            if (c == '/' || c == '.') {
                next = true;
                continue;
            }
            Validate.isTrue(Character.isJavaIdentifierPart(c), unexpected, c, pos, result);
        }

        result = result.replace('.', '/');
        final int last = result.length() - 1;
        if (result.charAt(last) == '/') {
            result = result.substring(0, last);
        }
        return result;
    }

    /**
     * Parse a number of Java types speciified as a comma-delimited
     * {@link String} of fully-qualified or internal names (i.e., slashes are
     * legal).
     * @param types to parse
     * @param cl {@link ClassLoader} to search
     * @return {@link Set} of {@link Class}
     */
    static Set<Class<?>> parseTypes(String types, ClassLoader cl) {
        final Set<Class<?>> result = new LinkedHashSet<Class<?>>();
        for (String s : StringUtils.splitByWholeSeparatorPreserveAllTokens(types, ",")) {
            try {
                result.add(ClassUtils.getClass(cl, s.trim().replace('/', '.')));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return result;
    }
}
