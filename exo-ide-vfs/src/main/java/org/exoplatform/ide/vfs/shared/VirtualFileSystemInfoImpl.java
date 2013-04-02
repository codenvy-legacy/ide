/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Describe virtual file system and its capabilities.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VirtualFileSystemInfo.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class VirtualFileSystemInfoImpl implements VirtualFileSystemInfo {

    private boolean versioningSupported;

    private boolean lockSupported;

    private String anonymousPrincipal;

    private String anyPrincipal;

    private Collection<String> permissions;

    private ACLCapability aclCapability;

    private QueryCapability queryCapability;

    private Folder root;

    private String id;

    /**
     * Templates of URL than can be used by client to manage virtual file system. Templates may contains parameters. It
     * is path or
     * query segments like next: [parameter]. Client should replace parameters by corresponded value or remove it from
     * template if
     * there is now value for it. Example:
     * <p/>
     * <pre>
     * http://localhost/service/vfs/jcr/file/[parentId]?name=[name]&mediaType=[mediaType]
     * become to
     * http://localhost/service/vfs/jcr/file/MyFolder001?name=NewFile.txt&mediaType=text/plain
     * </pre>
     */
    private Map<String, Link> urlTemplates;

    public VirtualFileSystemInfoImpl(String id,
                                     boolean versioningSupported,
                                     boolean lockSupported,
                                     String anonymousPrincipal,
                                     String anyPrincipal,
                                     Collection<String> permissions,
                                     ACLCapability aclCapability,
                                     QueryCapability queryCapability,
                                     Map<String, Link> urlTemplates,
                                     Folder root) {
        this.versioningSupported = versioningSupported;
        this.lockSupported = lockSupported;
        this.anonymousPrincipal = anonymousPrincipal;
        this.anyPrincipal = anyPrincipal;
        this.permissions = permissions;
        this.aclCapability = aclCapability;
        this.queryCapability = queryCapability;
        this.urlTemplates = urlTemplates;
        this.root = root;
        this.id = id;
    }

    public VirtualFileSystemInfoImpl() {
        this("default",
             false,
             false,
             ANONYMOUS_PRINCIPAL,
             ANY_PRINCIPAL,
             new ArrayList<String>(),
             ACLCapability.NONE,
             QueryCapability.NONE,
             null,
             null);
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
    public boolean isVersioningSupported() {
        return versioningSupported;
    }

    @Override
    public void setVersioningSupported(boolean versioningSupported) {
        this.versioningSupported = versioningSupported;
    }

    @Override
    public boolean isLockSupported() {
        return lockSupported;
    }

    @Override
    public void setLockSupported(boolean lockSupported) {
        this.lockSupported = lockSupported;
    }

    @Override
    public String getAnonymousPrincipal() {
        return anonymousPrincipal;
    }

    @Override
    public void setAnyPrincipal(String anyPrincipal) {
        this.anyPrincipal = anyPrincipal;
    }

    @Override
    public String getAnyPrincipal() {
        return anyPrincipal;
    }

    @Override
    public void setAnonymousPrincipal(String anonymousPrincipal) {
        this.anonymousPrincipal = anonymousPrincipal;
    }

    @Override
    public Collection<String> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public ACLCapability getAclCapability() {
        return aclCapability;
    }

    @Override
    public void setAclCapability(ACLCapability aclCapability) {
        this.aclCapability = aclCapability;
    }

    @Override
    public QueryCapability getQueryCapability() {
        return queryCapability;
    }

    @Override
    public void setQueryCapability(QueryCapability queryCapability) {
        this.queryCapability = queryCapability;
    }

    @Override
    public Map<String, Link> getUrlTemplates() {
        if (urlTemplates == null) {
            urlTemplates = new HashMap<String, Link>();
        }
        return urlTemplates;
    }

    @Override
    public void setUrlTemplates(Map<String, Link> uriTemplates) {
        this.urlTemplates = uriTemplates;
    }

    @Override
    public Folder getRoot() {
        return root;
    }

    @Override
    public void setRoot(Folder root) {
        this.root = root;
    }
}
