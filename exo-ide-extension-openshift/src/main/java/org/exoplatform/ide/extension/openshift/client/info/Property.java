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
package org.exoplatform.ide.extension.openshift.client.info;

/**
 * Aplication's property.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 3:03:14 PM anya $
 */
public class Property {
    /** Property's name. */
    private String name;

    /** Property's value. */
    private String value;

    /**
     * @param name
     *         property's name
     * @param value
     *         property's value
     */
    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the value */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *         the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
