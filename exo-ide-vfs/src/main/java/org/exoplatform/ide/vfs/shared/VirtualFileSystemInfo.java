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

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface VirtualFileSystemInfo {
    String ANONYMOUS_PRINCIPAL = "anonymous";
    String ANY_PRINCIPAL       = "any";

    String getId();

    void setId(String id);

    boolean isVersioningSupported();

    void setVersioningSupported(boolean versioningSupported);

    boolean isLockSupported();

    void setLockSupported(boolean lockSupported);

    String getAnonymousPrincipal();

    void setAnyPrincipal(String anyPrincipal);

    String getAnyPrincipal();

    void setAnonymousPrincipal(String anonymousPrincipal);

    Collection<String> getPermissions();

    void setPermissions(Collection<String> permissions);

    ACLCapability getAclCapability();

    void setAclCapability(ACLCapability aclCapability);

    QueryCapability getQueryCapability();

    void setQueryCapability(QueryCapability queryCapability);

    Map<String, Link> getUrlTemplates();

    void setUrlTemplates(Map<String, Link> uriTemplates);

    Folder getRoot();

    void setRoot(Folder root);

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

        /** @see Enum#toString() */
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

        /** @see Enum#toString() */
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
        /** Update item permissions (ACL). */
        UPDATE_ACL("update_acl"),
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

        /** @see Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }
}
