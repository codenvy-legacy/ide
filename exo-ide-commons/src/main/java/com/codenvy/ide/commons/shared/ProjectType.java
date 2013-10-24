/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.commons.shared;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum ProjectType {
    PHP("PHP"),
    WAR("War"),
    JAR("Jar"),
    JAVASCRIPT("JavaScript"),
    PYTHON("Python"),
    RUBY_ON_RAILS("Rails"),
    SPRING("Spring"),
    MULTI_MODULE("Maven Multi-module"),
    DEFAULT("default"),
    NODE_JS("nodejs"),
    ANDROID("Android"),
    GOOGLE_MBS_ANDROID("google-mbs-client-android"),
    JSP("Servlet/JSP");

    private final String value;

    private ProjectType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static ProjectType fromValue(String value) {
        for (ProjectType v : ProjectType.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
