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
    /** Id of object. */
    protected String id;

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

    /** @return id of object */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id of object
     */
    public void setId(String id) {
        this.id = id;
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
        return "Resource [id=" + id + ", name=" + name + ", type=" + resourceType + ']';
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
