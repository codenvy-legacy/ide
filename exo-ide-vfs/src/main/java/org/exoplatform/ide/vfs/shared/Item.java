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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of abstract item used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Item {
    /** @return id of virtual file system that contains object */
    String getVfsId();

    void setVfsId(String vfsId);

    /** @return id of object */
    String getId();

    /**
     * @param id
     *         the id of object
     */
    void setId(String id);

    /** @return name of object */
    String getName();

    /**
     * @param name
     *         the name of object
     */
    void setName(String name);

    /** @return type of item */
    ItemType getItemType();

    /** @return path */
    String getPath();

    /**
     * @param path
     *         the path
     */
    void setPath(String path);

    /** @return id of parent folder and <code>null</code> if current item is root folder */
    String getParentId();

    /**
     * @param parentId
     *         id of parent folder and <code>null</code> if current item is root folder
     */
    void setParentId(String parentId);

    /** @return creation date */
    long getCreationDate();

    /**
     * @param creationDate
     *         the creation date
     */
    void setCreationDate(long creationDate);

    /** @return media type */
    String getMimeType();

    /**
     * @param mimeType
     *         media type
     */
    void setMimeType(String mimeType);

    /**
     * Other properties.
     *
     * @return properties. If there is no properties then empty list returned, never <code>null</code>
     */
    List<Property> getProperties();

    /**
     * Get single property with specified name.
     *
     * @param name
     *         name of property
     * @return property or <code>null</code> if there is not property with specified name
     */
    Property getProperty(String name);

    /** @see #getProperties() */
    void setProperties(List<Property> properties);

    /**
     * Check does item has property with specified name.
     *
     * @param name
     *         name of property
     * @return <code>true</code> if item has property <code>name</code> and <code>false</code> otherwise
     */
    boolean hasProperty(String name);

    /**
     * Get value of property <code>name</code>. It is shortcut for:
     * <pre>
     *    String name = ...
     *    Item item = ...
     *    Property property = item.getProperty(name);
     *    String value;
     *    if (property != null)
     *       value = property.getValue().get(0);
     *    else
     *       value = null;
     *    ...
     * </pre>
     *
     * @param name
     *         property name
     * @return value of property with specified name or <code>null</code>
     */
    String getPropertyValue(String name);

    /**
     * Get set of property values
     *
     * @param name
     *         property name
     * @return set of property values or <code>null</code> if property does not exists
     * @see #getPropertyValue(String)
     */
    List<String> getPropertyValues(String name);

    /**
     * Links for retrieved or(and) manage item.
     *
     * @return links map. Never <code>null</code> but empty map instead
     */
    Map<String, Link> getLinks();

    /** @see #getLinks() */
    void setLinks(Map<String, Link> links);

    /** @return set of relations */
    Set<String> getLinkRelations();

    /**
     * @param rel
     *         relation string
     * @return corresponding hyperlink or null if no such relation found
     */
    Link getLinkByRelation(String rel);

    /**
     * Get permissions of current user. Current user is user who retrieved this item.
     *
     * @return set of permissions of current user.
     * @see VirtualFileSystemInfo.BasicPermissions
     */
    Set<String> getPermissions();

    /** @see #getPermissions() */
    void setPermissions(Set<String> permissions);
}
