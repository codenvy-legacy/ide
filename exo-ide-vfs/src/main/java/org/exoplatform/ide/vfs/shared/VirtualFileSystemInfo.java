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
