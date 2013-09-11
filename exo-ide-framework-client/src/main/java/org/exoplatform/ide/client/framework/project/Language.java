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
 * Defined languages.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:15:43 PM anya $
 */
public enum Language {
    PHP("PHP"), GROOVY("Groovy"), JAVA("Java"), JAVASCRIPT("JavaScript"), NODE_JS("Node.js"), PYTHON("Python"), RUBY("Ruby");

    private String value;

    private Language(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * @param v
     *         project's type value
     * @return {@link ProjectType}
     */
    public static Language fromValue(String v) {
        for (Language c : Language.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
