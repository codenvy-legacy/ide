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

import org.exoplatform.ide.vfs.server.VirtualFileSystemUser;
import org.exoplatform.ide.vfs.server.VirtualFileSystemUserContext;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class MemoryFileSystemContext {
    static final String ROOT_FOLDER_ID = ObjectIdGenerator.generateId();

    private final ConcurrentMap<String, MemoryItem> entries;
    private final MemoryFolder                      root;
    private final VirtualFileSystemUserContext      userContext;

    public MemoryFileSystemContext() {
        root = new MemoryFolder(ROOT_FOLDER_ID, "");
        entries = new ConcurrentHashMap<String, MemoryItem>();
        entries.put(root.getId(), root);
        userContext = VirtualFileSystemUserContext.newInstance();
    }

    public MemoryFolder getRoot() {
        return root;
    }

    public void putItem(MemoryItem item) throws VirtualFileSystemException {
        if (item.isFolder()) {
            final Map<String, MemoryItem> flatten = new HashMap<String, MemoryItem>();
            item.accept(new MemoryItemVisitor() {
                @Override
                public void visit(MemoryItem i) throws VirtualFileSystemException {
                    if (i.isFolder()) {
                        for (MemoryItem ii : ((MemoryFolder)i).getChildren()) {
                            ii.accept(this);
                        }
                        flatten.put(i.getId(), i);
                    } else {
                        flatten.put(i.getId(), i);
                    }
                }
            });
            entries.putAll(flatten);
        } else {
            entries.put(item.getId(), item);
        }
    }

    public MemoryItem getItem(String id) {
        return entries.get(id);
    }

    public MemoryItem getItemByPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Item path may not be null. ");
        }
        if ("/".equals(path)) {
            return getRoot();
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        MemoryItem item = getRoot();
        String[] split = path.split("/");
        for (int i = 0, length = split.length; item != null && i < length; i++) {
            String name = split[i];
            if (item.isFolder()) {
                item = ((MemoryFolder)item).getChild(name);
            }
        }
        return item;
    }

    public void deleteItem(String id) {
        entries.remove(id);
    }

    public void deleteItems(Collection<String> ids) {
        entries.keySet().removeAll(ids);
    }

    public boolean hasPermission(MemoryItem object, VirtualFileSystemInfo.BasicPermissions permission, boolean checkParent) {
        final VirtualFileSystemUser user = userContext.getVirtualFileSystemUser();
        MemoryItem current = object;
        while (current != null) {
            final Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> objectPermissions = current.getPermissions();
            if (!objectPermissions.isEmpty()) {
                Set<VirtualFileSystemInfo.BasicPermissions> userPermissions =
                        objectPermissions.get(new PrincipalImpl(user.getUserId(), Principal.Type.USER));
                if (userPermissions != null) {
                    return userPermissions.contains(permission) || userPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL);
                }
                Collection<String> groups = user.getGroups();
                if (!groups.isEmpty()) {
                    for (String group : groups) {
                        userPermissions = objectPermissions.get(new PrincipalImpl(group, Principal.Type.GROUP));
                        if (userPermissions != null) {
                            return userPermissions.contains(permission) ||
                                   userPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL);
                        }
                    }
                }
                userPermissions = objectPermissions.get(new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER));
                return userPermissions != null &&
                       (userPermissions.contains(permission) || userPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL));
            }
            if (checkParent) {
                current = current.getParent();
            } else {
                break;
            }
        }
        return true;
    }

    public VirtualFileSystemUser getCurrentVirtualFileSystemUser() {
        return userContext.getVirtualFileSystemUser();
    }
}
