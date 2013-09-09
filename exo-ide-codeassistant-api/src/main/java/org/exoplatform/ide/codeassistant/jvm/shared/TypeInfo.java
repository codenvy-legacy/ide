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

import java.util.List;

/**
 *
 */
public interface TypeInfo extends ShortTypeInfo {

    /** @return the fields */
    List<FieldInfo> getFields();

    /** @return the interfaces */
    List<String> getInterfaces();

    /** @return the methods */
    List<MethodInfo> getMethods();

    /** @return the superClass */
    String getSuperClass();

    /** @return type nested types or <code>null</code> */
    List<Member> getNestedTypes();

    /**
     * @param fields
     *         the fields to set
     */
    void setFields(List<FieldInfo> fields);

    /**
     * @param interfaces
     *         the interfaces to set
     */
    void setInterfaces(List<String> interfaces);

    /**
     * @param methods
     *         the methods to set
     */
    void setMethods(List<MethodInfo> methods);

    /**
     * @param superClass
     *         the superClass to set
     */
    void setSuperClass(String superClass);

    /**
     * @param types
     *         the nested types to set
     */
    void setNestedTypes(List<Member> types);

}