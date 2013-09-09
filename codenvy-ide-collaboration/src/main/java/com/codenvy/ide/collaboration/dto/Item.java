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
package com.codenvy.ide.collaboration.dto;

import com.codenvy.ide.dtogen.shared.*;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonStringMap;


/**
 * Partial copy of {@link org.exoplatform.ide.vfs.shared.Item}
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = RoutableDto.NON_ROUTABLE_TYPE)
public interface Item extends ServerToClientDto, CompactJsonDto {
    public enum ItemType {
        FILE("file"), FOLDER("folder"), PROJECT("project");

        private final String value;

        private ItemType(String value) {
            this.value = value;
        }

        /** @return value of Type */
        public String value() {
            return value;
        }

        /**
         * Get Type instance from string value.
         *
         * @param value
         *         string value
         * @return Type
         * @throws IllegalArgumentException
         *         if there is no corresponded Type for specified <code>value</code>
         */
        public static ItemType fromValue(String value) {
            String v = value.toLowerCase();
            for (ItemType e : ItemType.values()) {
                if (e.value.equals(v)) {
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

    /** @return id of object */
    @SerializationIndex(1)
    String getId();

    /** @return name of object */
    @SerializationIndex(2)
    String getName();

    /** @return type of item */
    @SerializationIndex(3)
    ItemType getItemType();

    /** @return path */
    @SerializationIndex(4)
    String getPath();

    /** @return id of parent folder and <code>null</code> if current item is root folder */
    @SerializationIndex(5)
    String getParentId();

    /** @return media type */
    @SerializationIndex(6)
    String getMimeType();

    /**
     * Other properties.
     *
     * @return properties. If there is no properties then empty list returned, never <code>null</code>
     */
    @SerializationIndex(7)
    JsonArray<Property> getProperties();


    /**
     * Links for retrieved or(and) manage item.
     *
     * @return links map. Never <code>null</code> but empty map instead
     */
    @SerializationIndex(8)
    JsonStringMap<Link> getLinks();

}
