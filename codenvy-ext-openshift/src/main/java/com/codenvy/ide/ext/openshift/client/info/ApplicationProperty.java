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
package com.codenvy.ide.ext.openshift.client.info;

/**
 * Wrapper for property which contain property name and value.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationProperty {
    private String propertyName;

    private String propertyValue;

    /**
     * Construct property.
     *
     * @param propertyName
     *         property name
     * @param propertyValue
     *         property value
     */
    public ApplicationProperty(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    /**
     * Get property name.
     *
     * @return String representation for property name.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Get property value.
     *
     * @return String representation for property value.
     */
    public String getPropertyValue() {
        return propertyValue;
    }
}
