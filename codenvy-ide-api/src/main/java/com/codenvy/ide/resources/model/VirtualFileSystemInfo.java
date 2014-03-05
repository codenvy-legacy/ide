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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

/**
 * Describe virtual file system and its capabilities.
 *
 * @author Andrey Parfonov
 */
public class VirtualFileSystemInfo {
    /** ACL capabilities. */
    public enum ACLCapability {
        /** ACL is not supported. */
        NONE("none"),
        /** ACL may be only discovered but can't be changed over virtual file system API. */
        READ("read"),
        /** ACL may be discovered and managed. */
        MANAGE("manage");

        private final String value;

        private ACLCapability(String value) {
            this.value = value;
        }

        /** @return value of ACLCapabilities */
        public String value() {
            return value;
        }

        /**
         * Get ACLCapabilities instance from string value.
         *
         * @param value
         *         string value
         * @return ACLCapabilities
         * @throws IllegalArgumentException
         *         if there is no corresponded ACLCapabilities for specified <code>value</code>
         */
        public static ACLCapability fromValue(String value) {
            for (ACLCapability e : ACLCapability.values()) {
                if (e.value.equals(value)) {
                    return e;
                }
            }
            throw new IllegalArgumentException(value);
        }

        /** @see java.lang.Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }

    /** Query capabilities. */
    public enum QueryCapability {
        /** Query is not supported. */
        NONE("none"),
        /** Query supported for properties only. */
        PROPERTIES("properties"),
        /** Full text search supported only. */
        FULLTEXT("fulltext"),
        /** Both queries are supported but not in one statement. */
        BOTHSEPARATE("bothseparate"),
        /** Both queries are supported in one statement. */
        BOTHCOMBINED("bothcombined");

        private final String value;

        private QueryCapability(String value) {
            this.value = value;
        }

        /** @return value of QueryCapability */
        public String value() {
            return value;
        }

        /**
         * Get QueryCapability instance from string value.
         *
         * @param value
         *         string value
         * @return QueryCapability
         * @throws IllegalArgumentException
         *         if there is no corresponded QueryCapability for specified <code>value</code>
         */
        public static QueryCapability fromValue(String value) {
            for (QueryCapability e : QueryCapability.values()) {
                if (e.value.equals(value)) {
                    return e;
                }
            }
            throw new IllegalArgumentException(value);
        }

        /** @see java.lang.Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }

    /** Basic permissions. */
    public enum BasicPermissions {
        /** Read permission. */
        READ("read"),
        /** Write permission. */
        WRITE("write"),
        /** All. Any operation allowed. */
        ALL("all");

        private final String value;

        private BasicPermissions(String value) {
            this.value = value;
        }

        /** @return value of BasicPermissions */
        public String value() {
            return value;
        }

        /**
         * Get BasicPermissions instance from string value.
         *
         * @param value
         *         string value
         * @return BasicPermissions
         * @throws IllegalArgumentException
         *         if there is no corresponded BasicPermissions for specified <code>value</code>
         */
        public static BasicPermissions fromValue(String value) {
            for (BasicPermissions e : BasicPermissions.values()) {
                if (e.value.equals(value)) {
                    return e;
                }
            }
            throw new IllegalArgumentException(value);
        }

        /** @see java.lang.Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }

    public static final String ANONYMOUS_PRINCIPAL = "anonymous";

    public static final String ANY_PRINCIPAL = "any";

    private boolean versioningSupported;

    private boolean lockSupported;

    private String anonymousPrincipal;

    private String anyPrincipal;

    private Array<String> permissions;

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
    private StringMap<Link> urlTemplates;

    public VirtualFileSystemInfo(String id, boolean versioningSupported, boolean lockSupported,
                                 String anonymousPrincipal, String anyPrincipal, Array<String> permissions, ACLCapability aclCapability,
                                 QueryCapability queryCapability, StringMap<Link> urlTemplates, Folder root) {
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

    public VirtualFileSystemInfo() {
        this("default", false, false, ANONYMOUS_PRINCIPAL, ANY_PRINCIPAL, Collections.<String>createArray(),
             ACLCapability.NONE, QueryCapability.NONE, null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isVersioningSupported() {
        return versioningSupported;
    }

    public void setVersioningSupported(boolean versioningSupported) {
        this.versioningSupported = versioningSupported;
    }

    public boolean isLockSupported() {
        return lockSupported;
    }

    public void setLockSupported(boolean lockSupported) {
        this.lockSupported = lockSupported;
    }

    public String getAnonymousPrincipal() {
        return anonymousPrincipal;
    }

    public void setAnyPrincipal(String anyPrincipal) {
        this.anyPrincipal = anyPrincipal;
    }

    public String getAnyPrincipal() {
        return anyPrincipal;
    }

    public void setAnonymousPrincipal(String anonymousPrincipal) {
        this.anonymousPrincipal = anonymousPrincipal;
    }

    public Array<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Array<String> permissions) {
        this.permissions = permissions;
    }

    public ACLCapability getAclCapability() {
        return aclCapability;
    }

    public void setAclCapability(ACLCapability aclCapability) {
        this.aclCapability = aclCapability;
    }

    public QueryCapability getQueryCapability() {
        return queryCapability;
    }

    public void setQueryCapability(QueryCapability queryCapability) {
        this.queryCapability = queryCapability;
    }

    public StringMap<Link> getUrlTemplates() {
        if (urlTemplates == null) {
            urlTemplates = Collections.createStringMap();
        }
        return urlTemplates;
    }

    public void setUrlTemplates(StringMap<Link> uriTemplates) {
        this.urlTemplates = uriTemplates;
    }

    public Folder getRoot() {
        return root;
    }

    public void setRoot(Folder root) {
        this.root = root;
    }
}
