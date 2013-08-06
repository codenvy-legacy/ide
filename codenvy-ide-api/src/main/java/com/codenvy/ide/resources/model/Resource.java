/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.resources.model;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;

/**
 * Resource's superclass.
 * Not intended to be extended by client code. Use File, Folder and Project as superclass.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
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

    /** Creation date in long format. */
    protected long creationDate;

    /** Links. */
    protected JsonStringMap<Link> links;

    /** Parent Project */
    protected Project project;

    /** Parent Folder */
    protected Folder parent;

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
    public String getRealtivePath() {
        if (parent != null && !(parent instanceof Project)) {
            return parent.getRealtivePath() + "/" + name;
        } else {
            return name;
        }
    }

    /** @return creation date */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate
     *         the creation date
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
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

    /**
     * Links for retrieved or(and) manage item.
     *
     * @return links map. Never <code>null</code> but empty map instead
     */
    public JsonStringMap<Link> getLinks() {
        if (links == null) {
            links = JsonCollections.createStringMap();
        }
        return links;
    }

    /**
     * @param rel
     *         relation string
     * @return corresponding hyperlink or null if no such relation found
     */
    public Link getLinkByRelation(String rel) {
        return getLinks().get(rel);
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
     * @param proj
     */
    public void setProject(Project proj) {
        this.project = proj;

    }

    /**
     * Get parent Folder
     *
     * @return
     */
    public final Folder getParent() {
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

    /** @return  */
    public abstract boolean isFile();

    /** @return  */
    public abstract boolean isFolder();

}
