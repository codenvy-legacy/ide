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
 * Represent information about class method. Can be transform to JSON. <code>
 * {
 * "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 * "exceptionTypes": [],
 * "declaringClass": "java.lang.String",
 * "name": "equals",
 * "genericParameterTypes": "(java.lang.Object)",
 * "modifiers": 1,
 * "returnType": "boolean",
 * "parameterTypes": "(Object)",
 * "genericReturnType": "boolean"
 * }
 * </code>
 */
public interface MethodInfo extends Member {

    String getDeclaringClass();

    List<String> getExceptionTypes();

    /** @return the parameterNames */
    List<String> getParameterNames();

    /** @return the parameterTypes */
    List<String> getParameterTypes();

    String getReturnType();

    /** @return the method's descriptor. */
    String getDescriptor();

    /** return the signature of the method. May be <tt>null</tt>. */
    String getSignature();

    /**
     * The default value of this annotation interface method.
     * May be <tt>null</tt>.
     *
     * @return
     */
    AnnotationValue getAnnotationDefault();

    /** @return the isConstructor */
    boolean isConstructor();

    /**
     * @param isConstructor
     *         the isConstructor to set
     */
    void setConstructor(boolean isConstructor);

    void setDeclaringClass(String declaringClass);

    void setExceptionTypes(List<String> exceptionTypes);

    /**
     * @param parameterNames
     *         the parameterNames to set
     */
    void setParameterNames(List<String> parameterNames);

    /**
     * @param parameterTypes
     *         the parameterTypes to set
     */
    void setParameterTypes(List<String> parameterTypes);

    void setReturnType(String returnType);

    void setDescriptor(String descriptor);

    /**
     * set signature of the method. May be <tt>null</tt>.
     *
     * @param signature
     */
    void setSignature(String signature);

    void setAnnotationDefault(AnnotationValue value);

}