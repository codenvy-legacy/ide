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
package com.codenvy.ide.ext.aws.client.ec2;

/**
 * Wrapper to store ec2 tags name and value.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Ec2Tag {
    private String tagName;

    private String tagValue;

    /**
     * Construct property.
     *
     * @param tagName
     *         property name
     * @param tagValue
     *         property value
     */
    public Ec2Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    /**
     * Get property name.
     *
     * @return String representation for property name.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Get property value.
     *
     * @return String representation for property value.
     */
    public String getTagValue() {
        return tagValue;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }
}