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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Jul 26, 2012 12:40:03 PM anya $
 */
public interface Template {

    /** @return the isDefault */
    Boolean isDefault();

    /**
     * @param isDefault
     *         the isDefault to set
     */
    void setDefault(Boolean isDefault);

    /** @return the name */
    String getName();

    /**
     * @param name
     *         the name to set
     */
    void setName(String name);

    /** @return the description */
    String getDescription();

    /**
     * @param description
     *         the description to set
     */
    void setDescription(String description);

    /** @return the nodeName */
    String getNodeName();

    /**
     * @param nodeName
     *         the nodeName to set
     */
    void setNodeName(String nodeName);

    ImageResource getIcon();

}