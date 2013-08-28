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
package org.exoplatform.ide.client.framework.project;

/**
 * Defined types of projects.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:15:43 PM anya $
 */
@Deprecated
public enum ProjectType {
    PHP("PHP"),
    JSP("Servlet/JSP"),
    JAVA("Java"),
    JAR("Jar"),
    WAR("War"),
    JAVASCRIPT("JavaScript"),
    NODE_JS("nodejs"),
    PYTHON("Python"),
    DJANGO("Django"),
    RUBY_ON_RAILS("Rails"),
    RUBY("Ruby"),
    SPRING("Spring"),
    DEFAULT("default"),
    MultiModule("Maven Multi-module");

    /** Project's type name. */
    private String type;

    /**
     * @param type project's type name
     */
    private ProjectType(String type) {
        this.type = type;
    }

    /** @return {@link String} project's type value */
    public String value() {
        return type;
    }

    /**
     * @param v project's type value
     * @return {@link ProjectType}
     */
    public static ProjectType fromValue(String v) {
        for (ProjectType c : ProjectType.values()) {
            if (c.type.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return value();
    }
}
