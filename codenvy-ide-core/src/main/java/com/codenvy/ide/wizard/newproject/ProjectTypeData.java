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
package com.codenvy.ide.wizard.newproject;

import com.google.gwt.resources.client.ImageResource;

/**
 * Aggregate information about registered project type.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ProjectTypeData {
    private String        typeName;
    private String        title;
    private ImageResource icon;

    /**
     * Create project type.
     *
     * @param typeName
     * @param title
     * @param icon
     */
    public ProjectTypeData(String typeName, String title, ImageResource icon) {
        this.typeName = typeName;
        this.title = title;
        this.icon = icon;
    }

    /** @return the project type's name */
    public String getTypeName() {
        return typeName;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /** @return the icon */
    public ImageResource getIcon() {
        return icon;
    }
}