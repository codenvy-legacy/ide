/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.resources.model;

import com.codenvy.api.core.rest.shared.dto.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource's superclass.
 * Not intended to be extended by client code. Use File, Folder and Project as superclass.
 *
 * @author Nikolay Zamosenchuk
 */
public abstract class Resource {

    /** Name of object. */
    protected String name;

    /** Type of object. */
    protected final String resourceType;

    /** Media type. */
    protected String mimeType;

    protected Object tags;

    /** Parent Project */
    protected Project project;

    /** Parent Folder */
    protected Folder parent;
    
    protected List<Link> links;

    /**
     * Create a Resource of given Type.
     * Not intended to be used by clients.
     */
    protected Resource(String itemType) {
        this.resourceType = itemType;
    }

    /** @return name of object */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name of object
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return type of item */
    public String getResourceType() {
        return resourceType;
    }

    /** @return path */
    public String getPath() {
        if (parent != null) {
            return parent.getPath() + "/" + name;
        } else {
            return name;
        }
    }

    /**
     * Returns Project-relative path of the {@link Resource}
     *
     * @return the path of the {@link Resource} in relation to {@link Project}
     */
    public String getRelativePath() {
        if (parent != null && !(parent instanceof Project)) {
            return parent.getRelativePath() + "/" + name;
        } else {
            return name;
        }
    }

    /** @return media type */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType
     *         media type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Resource [name=" + name + ", path=" + getPath() + ", type=" + resourceType + ']';
    }

    /**
     * Get parent Project
     *
     * @return
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set parent Project
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get parent Folder
     *
     * @return
     */
    public Folder getParent() {
        return parent;
    }

    /**
     * Set parent Folder
     *
     * @param parent
     */
    public void setParent(Folder parent) {
        this.parent = parent;
    }

    /**
     * Generic object tag. Can be used by UI components
     * TODO : REMOVE
     *
     * @param tag
     */
    public void setTag(Object tag) {
        this.tags = tag;
    }

    /**
     * Generic object tag. Can be used by UI components
     * TODO : REMOVE
     *
     * @return
     */
    public Object getTag() {
        return tags;
    }
    
    public List<Link> getLinks() {
        if (links == null) {
            links = new ArrayList<Link>();
        }
        return links;
    }

    public void setLinks(List<Link> v) {
        this.links = v;
    }

    /** @return  */
    public abstract boolean isFile();

    /** @return  */
    public abstract boolean isFolder();

}
