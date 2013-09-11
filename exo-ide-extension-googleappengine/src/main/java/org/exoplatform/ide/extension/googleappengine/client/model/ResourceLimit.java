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
package org.exoplatform.ide.extension.googleappengine.client.model;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 31, 2012 4:47:42 PM anya $
 */
public class ResourceLimit {
    private String name;

    private Long value;

    public ResourceLimit() {
    }

    public ResourceLimit(String name, Long value) {
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
    public Long getValue() {
        return value;
    }

    /**
     * @param value
     *         the value to set
     */
    public void setValue(Long value) {
        this.value = value;
    }

}
