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
package com.codenvy.ide.ext.java.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Represent information about class field. Can be transform to JSON. Example of
 * JSON: <code>
 * {
 * "declaringClass": "java.lang.String",
 * "name": "CASE_INSENSITIVE_ORDER",
 * "modifiers": 25,
 * "type": "Comparator",
 * "descriptor":"Ljava/util/Comparator;",
 * "signature : "Ljava/util/Comparator<Ljava/lang/String;>"
 * }
 * </code>
 */
@DTO
public interface FieldInfo extends Member {

    String getDeclaringClass();

    String getType();

    /** return the signature of the field. May be <tt>null</tt>. */
    String getSignature();

    /** @return the field's descriptor. */
    String getDescriptor();

    /**
     * Should return field initial value description if exist, otherwise <code>null</code>.
     *
     * @return initial value;
     */
    String getValue();

    void setDeclaringClass(String declaringClass);

    void setType(String type);

    /**
     * set signature of the field. May be <tt>null</tt>.
     *
     * @param signature
     */
    void setSignature(String signature);

    /** @param descriptor */
    void setDescriptor(String descriptor);

    /** @param value */
    void setValue(String value);

}