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

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@DTO
public interface AnnotationValue {

    /**
     * Must return two element array.
     * First element name of primitive type:
     * <ul>
     * <li> char
     * <li> byte
     * <li> boolean
     * <li> int
     * <li>float
     * ...
     * </ul>
     * Second element is value of primitive type
     *
     * @return
     */
    List<String> getPrimitiveType();

    /**
     * Must return array with of values, where first value is Array type
     *
     * @return
     */
    List<String> getArrayType();

    String getClassSignature();

    /**
     * Must return two element array, where first element is FQN of enum,
     * second element is constant name;
     *
     * @return
     */
    List<String> getEnumConstant();

    Annotation getAnnotation();


    void setPrimitiveType(List<String> value);

    void setArrayType(List<String> value);

    void setClassSignature(String value);

    void setEnumConstant(List<String> value);

    void setAnnotation(Annotation annotation);


}
