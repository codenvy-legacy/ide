/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server.impl.memory.context;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class MemoryItem {
    private final String                             id;
    private final Map<String, Set<BasicPermissions>> permissionsMap;
    private final Map<String, List<String>>          properties;
    private final long                               creationDate;

    private MemoryFolder parent;
    private String       name;

    long lastModificationDate;

    MemoryItem(String id, String name) {
        this.id = id;
        this.name = name;
        this.permissionsMap = new HashMap<String, Set<BasicPermissions>>();
        this.properties = new HashMap<String, List<String>>();
        this.creationDate = this.lastModificationDate = System.currentTimeMillis();
    }

    public final MemoryFolder getParent() {
        return parent;
    }

    final void setParent(MemoryFolder parent) {
        this.parent = parent;
    }

    public abstract boolean isFile();

    public abstract boolean isFolder();

    public abstract boolean isProject();

    public final String getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    final void setName(String newName) throws VirtualFileSystemException {
        this.name = newName;
        lastModificationDate = System.currentTimeMillis();
    }

    public final String getMediaType() throws VirtualFileSystemException {
        List<Property> properties = getProperties(PropertyFilter.valueOf("vfs:mimeType"));
        if (properties.size() > 0) {
            List<String> values = properties.get(0).getValue();
            if (!(values == null || values.isEmpty())) {
                return values.get(0);
            }
        }
        return null;
    }

    public final void setMediaType(String mediaType) {
        updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", mediaType)));
        lastModificationDate = System.currentTimeMillis();
    }

    public final String getPath() {
        if (MemoryFileSystemContext.ROOT_FOLDER_ID.equals(id)) {
            return "/";
        }

        MemoryFolder parent = this.parent;
        if (parent == null) {
            return null; // item is not root folder but not added in tree yet.
        }

        String name = this.name;
        LinkedList<String> pathSegments = new LinkedList<String>();
        pathSegments.add(name);

        while (parent != null) {
            pathSegments.addFirst(parent.getName());
            parent = parent.getParent();
        }

        StringBuilder path = new StringBuilder();
        path.append('/');
        for (String seg : pathSegments) {
            if (path.length() > 1) {
                path.append('/');
            }
            path.append(seg);
        }
        return path.toString();
    }

    public final List<AccessControlEntry> getACL() {
        synchronized (permissionsMap) {
            if (permissionsMap.isEmpty()) {
                return Collections.emptyList();
            }
            List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(permissionsMap.size());
            for (Map.Entry<String, Set<BasicPermissions>> e : permissionsMap.entrySet()) {
                Set<BasicPermissions> basicPermissions = e.getValue();
                Set<String> plainPermissions = new HashSet<String>(basicPermissions.size());
                for (BasicPermissions permission : e.getValue()) {
                    plainPermissions.add(permission.value());
                }

                acl.add(new AccessControlEntryImpl(e.getKey(), plainPermissions));
            }
            return acl;
        }
    }

    public final void updateACL(List<AccessControlEntry> acl, boolean override) {
        Map<String, Set<BasicPermissions>> update = new HashMap<String, Set<BasicPermissions>>(acl.size());
        for (AccessControlEntry ace : acl) {
            String principal = ace.getPrincipal();
            Set<BasicPermissions> permissions = update.get(principal);
            if (permissions == null) {
                permissions = EnumSet.noneOf(BasicPermissions.class);
                update.put(principal, permissions);
            }
            if (!(ace.getPermissions() == null || ace.getPermissions().isEmpty())) {
                for (String strPermission : ace.getPermissions()) {
                    permissions.add(BasicPermissions.fromValue(strPermission));
                }
            }
        }

        synchronized (permissionsMap) {
            if (override) {
                permissionsMap.clear();
            }
            permissionsMap.putAll(update);
        }
        lastModificationDate = System.currentTimeMillis();
    }

    public final Map<String, Set<BasicPermissions>> getPermissions() {
        synchronized (permissionsMap) {
            Map<String, Set<BasicPermissions>> copy = new HashMap<String, Set<BasicPermissions>>(permissionsMap.size());
            for (Map.Entry<String, Set<BasicPermissions>> e : permissionsMap.entrySet()) {
                copy.put(e.getKey(), EnumSet.copyOf(e.getValue()));
            }
            return copy;
        }
    }

    public boolean hasPermissions(BasicPermissions permission, boolean checkParent) {
        final ConversationState cs = ConversationState.getCurrent();
        final Identity user = cs != null ? cs.getIdentity() : new Identity(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL);
        MemoryItem object = this;
        while (object != null) {
            final Map<String, Set<BasicPermissions>> objectPermissions = object.getPermissions();
            if (!objectPermissions.isEmpty()) {
                Set<BasicPermissions> userPermissions = objectPermissions.get(user.getUserId());
                if (userPermissions != null) {
                    return userPermissions.contains(permission) || userPermissions.contains(BasicPermissions.ALL);
                }
                Collection<String> roles = user.getRoles();
                if (!roles.isEmpty()) {
                    for (String role : roles) {
                        userPermissions = objectPermissions.get("role:" + role);
                        if (userPermissions != null) {
                            return userPermissions.contains(permission) || userPermissions.contains(BasicPermissions.ALL);
                        }
                    }
                }
                userPermissions = objectPermissions.get(VirtualFileSystemInfo.ANY_PRINCIPAL);
                return userPermissions != null &&
                       (userPermissions.contains(permission) || userPermissions.contains(BasicPermissions.ALL));
            }
            if (checkParent) {
                object = object.getParent();
            } else {
                break;
            }
        }
        return true;
    }

    public final List<Property> getProperties(PropertyFilter filter) {
        List<Property> result = new ArrayList<Property>();
        synchronized (properties) {
            for (Map.Entry<String, List<String>> e : properties.entrySet()) {
                String name = e.getKey();
                if (filter.accept(name)) {
                    List<String> value = e.getValue();
                    if (value != null) {
                        List<String> copy = new ArrayList<String>(value.size());
                        copy.addAll(value);
                        result.add(new PropertyImpl(name, copy));
                    } else {
                        result.add(new PropertyImpl(name, (String)null));
                    }
                }
            }
        }
        return result;
    }

    public final void updateProperties(List<Property> update) {
        synchronized (properties) {
            for (Property p : update) {
                String name = p.getName();
                List<String> value = p.getValue();
                if (value != null) {
                    List<String> copy = new ArrayList<String>(value.size());
                    copy.addAll(value);
                    properties.put(name, copy);
                } else {
                    properties.remove(name);
                }
            }
        }
        lastModificationDate = System.currentTimeMillis();
    }

    public final long getCreationDate() {
        return creationDate;
    }

    public final long getLastModificationDate() {
        return lastModificationDate;
    }

    public abstract MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException;

    public final void accept(MemoryItemVisitor visitor) throws VirtualFileSystemException {
        visitor.visit(this);
    }
}
