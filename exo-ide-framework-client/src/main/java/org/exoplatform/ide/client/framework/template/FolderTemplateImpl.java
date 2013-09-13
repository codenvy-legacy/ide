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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class FolderTemplateImpl extends AbstractTemplate implements FolderTemplate {
    private List<AbstractTemplate> children;

    public FolderTemplateImpl() {
    }

    public FolderTemplateImpl(String name) {
        super(name);
    }

    public FolderTemplateImpl(String name, String description, String nodeName, List<AbstractTemplate> children) {
        super(name, description, nodeName);
        this.children = children;
    }

    public FolderTemplateImpl(String name, String description, boolean isDefault) {
        super(name, description, isDefault);
    }

    /** @see org.exoplatform.ide.client.framework.template.FolderTemplate#getChildren() */
    @Override
    public List<AbstractTemplate> getChildren() {
        if (children == null) {
            children = new ArrayList<AbstractTemplate>();
        }
        return children;
    }

    /** @see org.exoplatform.ide.client.framework.template.FolderTemplate#setChildren(java.util.List) */
    @Override
    public void setChildren(List<AbstractTemplate> children) {
        this.children = children;
    }

    /** @see org.exoplatform.ide.client.framework.template.AbstractTemplate#getIcon() */
    @Override
    public ImageResource getIcon() {
        //TODO
        return null;
    }
}
