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
package org.exoplatform.ide.vfs.shared;

import java.util.*;

/**
 * Representation of abstract item used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Item.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public abstract class ItemImpl implements Item {
    /** Id of virtual file system that contains object. */
    protected String vfsId;

    /** Id of object. */
    protected String id;

    /** Name of object. */
    protected String name;

    /** Type of object. */
    protected final ItemType itemType;

    /** Media type. */
    protected String mimeType;

    /** Path. */
    protected String path;

    /** Parent ID. Must be <code>null</code> if item is root folder. */
    protected String parentId;

    /** Creation date in long format. */
    protected long creationDate;

    /** Properties. */
    @SuppressWarnings("rawtypes")
    protected List<Property> properties;

    /** Links. */
    protected Map<String, Link> links;

    protected Set<String> permissions;

    /**
     * @param id
     *         id of item
     * @param name
     *         name of item
     * @param itemType
     *         type of item
     * @param mimeType
     *         the media type
     * @param path
     *         path of item
     * @param parentId
     *         id of parent folder. May be <code>null</code> if current item is root folder
     * @param creationDate
     *         creation date in long format
     * @param properties
     *         other properties of object
     * @param links
     *         hyper-links for retrieved or(and) manage item
     */
    @SuppressWarnings("rawtypes")
    public ItemImpl(String vfsId, String id, String name, ItemType itemType, String mimeType, String path, String parentId,
                    long creationDate, List<Property> properties, Map<String, Link> links) {
        this.vfsId = vfsId;
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.mimeType = mimeType;
        this.path = path;
        this.parentId = parentId;
        this.creationDate = creationDate;
        this.properties = properties;
        this.links = links;
    }

    public ItemImpl(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public String getVfsId() {
        return vfsId;
    }

    @Override
    public void setVfsId(String vfsId) {
        this.vfsId = vfsId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public long getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    @Override
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public Property getProperty(String name) {
        for (Property p : getProperties()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean hasProperty(String name) {
        return getProperty(name) != null;
    }

    @Override
    public String getPropertyValue(String name) {
        Property p = getProperty(name);
        if (p != null) {
            return p.getValue().get(0);
        }
        return null;
    }

    @Override
    public List<String> getPropertyValues(String name) {
        Property p = getProperty(name);
        if (p != null) {
            List<String> values = new ArrayList<String>(p.getValue().size());
            for (String v : p.getValue()) {
                values.add(v);
            }
            return values;
        }
        return null;
    }

    @Override
    public Map<String, Link> getLinks() {
        if (links == null) {
            links = new HashMap<String, Link>();
        }
        return links;
    }

    @Override
    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    @Override
    public Set<String> getLinkRelations() {
        return getLinks().keySet();
    }

    @Override
    public Link getLinkByRelation(String rel) {
        return getLinks().get(rel);
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Item [vfsId=" + vfsId + ", " + itemType + ", id=" + id + ", path=" + path + ']';
    }
}
