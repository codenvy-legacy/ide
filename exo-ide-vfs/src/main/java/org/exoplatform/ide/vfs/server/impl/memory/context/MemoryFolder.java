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
package org.exoplatform.ide.vfs.server.impl.memory.context;

import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFolder extends MemoryItem {
    private final Map<String, MemoryItem> children;

    public MemoryFolder(String name) {
        this(ObjectIdGenerator.generateId(), name);
    }

    MemoryFolder(String id, String name) {
        super(id, name);
        children = new LinkedHashMap<String, MemoryItem>();
    }

    @Override
    public final boolean isFile() {
        return false;
    }

    @Override
    public final boolean isFolder() {
        return true;
    }

    public List<MemoryItem> getChildren() {
        List<MemoryItem> copy;
        synchronized (children) {
            copy = new ArrayList<MemoryItem>(children.values());
        }
        return copy;
    }

    public void addChild(MemoryItem child) throws VirtualFileSystemException {
        String childName = child.getName();
        synchronized (children) {
            if (children.get(childName) != null) {
                throw new ItemAlreadyExistException("Item with the name '" + childName + "' already exists. ");
            }
            children.put(childName, child);
        }
        child.setParent(this);
    }

    public MemoryItem getChild(String name) {
        synchronized (children) {
            return children.get(name);
        }
    }

    public MemoryItem removeChild(String name) {
        synchronized (children) {
            MemoryItem removed = children.remove(name);
            if (removed != null) {
                removed.setParent(null);
            }
            return removed;
        }
    }

    public MemoryItem renameChild(String name, String newName) throws VirtualFileSystemException {
        synchronized (children) {
            MemoryItem child = children.get(name);
            if (child != null) {
                if (children.get(newName) != null) {
                    throw new ItemAlreadyExistException("Item with the name '" + newName + "' already exists. ");
                }
                children.remove(name);
                child.setName(newName);
                children.put(newName, child);
                return child;
            }
            throw new ItemNotFoundException("Not found child item with the name '" + name);
        }
    }

    @Override
    public MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException {
        MemoryFolder copy = new MemoryFolder(ObjectIdGenerator.generateId(), getName());
        for (MemoryItem i : getChildren()) {
            i.copy(copy);
        }
        copy.updateProperties(getProperties(PropertyFilter.ALL_FILTER));
        copy.updateACL(getACL(), true);
        parent.addChild(copy);
        return copy;
    }

    @Override
    public boolean isProject() {
        List<Property> properties;
        try {
            properties = getProperties(PropertyFilter.valueOf("vfs:mimeType"));
        } catch (InvalidArgumentException e) {
            // Should not happen.
            throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
        }
        if (properties.isEmpty()) {
            return false;
        }
        List<String> values = properties.get(0).getValue();
        return !(values == null || values.isEmpty()) && Project.PROJECT_MIME_TYPE.equals(values.get(0));
    }

    @Override
    public String toString() {
        return "MemoryFolder{" +
               "id='" + getId() + '\'' +
               ", path=" + getPath() +
               ", name='" + getName() + '\'' +
               ", isProject=" + isProject() +
               '}';
    }
}
