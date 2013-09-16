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
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class AbstractTemplate implements Template {

    private String name;

    private String description;

    private String nodeName;

    /** If template is default, than it must be created by server. If not default (user template), than it must be crated by client. */
    private boolean isDefault;

    public AbstractTemplate() {
    }

    public AbstractTemplate(String name) {
        this.name = name;
    }

    public AbstractTemplate(String name, String description, String nodeName) {
        this.name = name;
        this.description = description;
        this.nodeName = nodeName;
    }

    public AbstractTemplate(String name, String description, boolean isDefault) {
        this.name = name;
        this.description = description;
        this.isDefault = isDefault;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#isDefault() */
    @Override
    public Boolean isDefault() {
        return isDefault;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#setDefault(boolean) */
    @Override
    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#getDescription() */
    @Override
    public String getDescription() {
        return description;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#setDescription(java.lang.String) */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#getNodeName() */
    @Override
    public String getNodeName() {
        return nodeName;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#setNodeName(java.lang.String) */
    @Override
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /** @see org.exoplatform.ide.client.framework.template.Template#getIcon() */
    @Override
    public abstract ImageResource getIcon();

}
