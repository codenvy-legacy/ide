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
package org.exoplatform.ide.vfs.client.model;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.*;

import java.util.*;

public class FolderModel extends FolderImpl implements ItemContext {

    protected ItemList<Item> children = new ItemListImpl<Item>();

    protected ProjectModel project;

    protected FolderModel parent;

    protected boolean persisted;

    @SuppressWarnings("rawtypes")
    public FolderModel(String name, FolderModel parent) {
        super(null, null, name, FOLDER_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(),
              new ArrayList<Property>(), new HashMap<String, Link>());
        this.parent = parent;
        this.persisted = false;
    }

    @SuppressWarnings("rawtypes")
    public FolderModel(String name, FolderModel parent, Map<String, Link> links) {
        super(null, null, name, FOLDER_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(),
              new ArrayList<Property>(), links);
        this.parent = parent;
        this.persisted = false;
    }

    public FolderModel(JSONObject itemObject) {
        super();
        init(itemObject);
    }

    public FolderModel(Folder folder) {
        this(folder.getVfsId(), folder.getId(), folder.getName(), ItemType.FOLDER, FOLDER_MIME_TYPE, folder.getPath(), folder.getParentId(),
             folder
                     .getCreationDate(), folder.getProperties(), folder.getLinks());
    }

    public FolderModel(String vfsId, String id, String name, ItemType itemType, String mimeType, String path, String parentId,
                       long creationDate,
                       List<Property> properties, Map<String, Link> links) {
        super(vfsId, id, name, itemType, mimeType, path, parentId, creationDate, properties, links);
        this.persisted = true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init(JSONObject itemObject) {
        vfsId = itemObject.get("vfsId").isString().stringValue();
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        if (itemObject.get("mimeType").isString() != null)
            mimeType = itemObject.get("mimeType").isString().stringValue();
        path = itemObject.get("path").isString().stringValue();
        parentId = (itemObject.get("parentId").isNull() != null) ? null : itemObject.get("parentId").isString().stringValue();
        creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
        properties = (List)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
        permissions = JSONDeserializer.STRING_DESERIALIZER.toSet(itemObject.get("permissions"));
        persisted = true;
    }

    public FolderModel() {
        super();
    }

    /** @return the children */
    public ItemList<Item> getChildren() {
        return children;
    }

    /**
     * @param children
     *         the children to set
     */
    public void setChildren(ItemList<Item> children) {
        this.children = children;
    }

    @Override
    public ProjectModel getProject() {
        return project;
    }

    @Override
    public void setProject(ProjectModel proj) {
        this.project = proj;

    }

    @Override
    public FolderModel getParent() {
        return parent;
    }

    @Override
    public void setParent(FolderModel parent) {
        this.parent = parent;
    }

    @Override
    public boolean isPersisted() {
        return persisted;
    }

}
