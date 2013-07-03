/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.client.ec2;

/**
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