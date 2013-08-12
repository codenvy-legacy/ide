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

/**
 * Short information about class or interface. Contain fqn, short name,
 * modifiers Example : { "name": "java.lang.String", "modifiers": 0, "type":
 * "CLASS" }
 */
public interface ShortTypeInfo extends Member {

    String getType();

    void setType(String type);

    /** return the signature of the class. May be <tt>null</tt>. */
    String getSignature();


    /**
     * set signature of the class. May be <tt>null</tt>.
     *
     * @param signature
     */
    void setSignature(String signature);

}