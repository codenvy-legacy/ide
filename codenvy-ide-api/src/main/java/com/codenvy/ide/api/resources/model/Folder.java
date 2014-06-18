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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.json.client.JSONObject;

import javax.annotation.Nullable;

/**
 * Represents the folder containing {@link Resource}s.
 *
 * @author Nikolay Zamosenchuk
 */
public class Folder extends Resource {
    public static final String          FOLDER_MIME_TYPE = "text/directory";
    public static final String          TYPE             = "folder";
    private             Array<Resource> children         = Collections.createArray();

    /**
     * Empty instance of Folder.
     * Not intended to be used by clients.
     */
    protected Folder() {
        this(TYPE, FOLDER_MIME_TYPE);
    }

    /** For subclassing */
    protected Folder(String itemType, String mimeType) {
        super(itemType);
        this.mimeType = mimeType;
    }

    public Folder(ItemReference itemReference) {
        this();
        init(itemReference);
    }

    public Folder(JSONObject itemObject) {
        this();
        init(itemObject);
    }

    public void init(ItemReference itemReference) {
        id = itemReference.getId();
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
        setLinks(itemReference.getLinks());
    }

    public void init(JSONObject itemObject) {
        id = itemObject.containsKey("id") ? itemObject.get("id").isString().stringValue() : null;
        name = itemObject.containsKey("name") ? itemObject.get("name").isString().stringValue() : null;
        if (itemObject.containsKey("mimeType") && itemObject.get("mimeType").isString() != null) {
            mimeType = itemObject.get("mimeType").isString().stringValue();
        }
    }


    /** @return the children */
    public Array<Resource> getChildren() {
        return children;
    }

    /**
     * @param children
     *         the children to set
     */
    public void setChildren(Array<Resource> children) {
        this.children = children;
    }

    /**
     * Recursively looks for the Resource
     *
     * @param id
     * @return resource or null if not found
     */
    @Nullable
    public Resource findResourceById(String id) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getId().equals(id)) {
                return child;
            }

            if (child instanceof Folder) {
                Resource resourceById = ((Folder)child).findResourceById(id);
                if (resourceById != null) {
                    return resourceById;
                }
            }
        }
        return null;
    }

    /**
     * Looks for the child resource, without recursive calls.
     *
     * @param name
     * @return resource or null if not found
     */
    @Nullable
    public Resource findChildByName(String name) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Recursively looks for the Resource
     *
     * @param name
     * @param type
     * @return resource or null if not found
     */
    @Nullable
    public Resource findResourceByName(String name, String type) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getName().equals(name) && child.getResourceType().equals(type)) {
                return child;
            }

            if (child instanceof Folder) {
                Resource resourceById = ((Folder)child).findResourceByName(name, type);
                if (resourceById != null) {
                    return resourceById;
                }
            }
        }
        return null;
    }

    /**
     * Internal add to list.
     * Sets proper parent
     *
     * @param resource
     */
    public void addChild(Resource resource) {
        children.add(resource);
        resource.setParent(this);
    }

    /**
     * Internal remove from list
     *
     * @param resource
     */
    public void removeChild(Resource resource) {
        children.remove(resource);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFile() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFolder() {
        return true;
    }
}
