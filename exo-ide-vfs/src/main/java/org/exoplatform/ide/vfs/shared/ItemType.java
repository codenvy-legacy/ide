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

/**
 * Object types.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ItemType.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
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