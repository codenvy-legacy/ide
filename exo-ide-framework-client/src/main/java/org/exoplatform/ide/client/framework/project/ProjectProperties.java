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
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 26, 2012 10:31:58 AM anya $
 */
public enum ProjectProperties {
    TYPE("vfs:projectType"),
    TARGET("exoide:target"),
    JREBEL_COUNT("jrebelCount"),
    JREBEL("jrebel"),
    DESCRIPTION("exoide:projectDescription"),
    MIME_TYPE("vfs:mimeType");

    private String value;

    private ProjectProperties(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ProjectProperties fromValue(String v) {
        for (ProjectProperties c : ProjectProperties.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
