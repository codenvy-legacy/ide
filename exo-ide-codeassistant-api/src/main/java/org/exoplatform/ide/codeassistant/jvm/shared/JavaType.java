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
package org.exoplatform.ide.codeassistant.jvm.shared;


/** Created by The eXo Platform SAS. */
public enum JavaType {
    CLASS, INTERFACE, ANNOTATION, ENUM;

    /**
     * Return JavaType depends on the value of the class attribute. See private
     * constants in class Modifier and Class.
     */
    public static JavaType fromClassAttribute(int attr) {

        if ((0x00002000 & attr) != 0) {
            return ANNOTATION;
        } else if ((0x00000200 & attr) != 0) {
            return INTERFACE;
        } else if ((0x00004000 & attr) != 0) {
            return ENUM;
        } else {
            return CLASS;
        }
    }
}
