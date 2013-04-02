/*
 * Copyright (C) 2010 eXo Platform SAS.
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
