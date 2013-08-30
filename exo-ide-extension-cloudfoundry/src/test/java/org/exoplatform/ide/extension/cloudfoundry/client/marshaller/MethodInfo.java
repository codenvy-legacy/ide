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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.util.List;


/**
 * Represent information about class method. Can be transform to JSON. <code>
 * {
 * "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 * "genericExceptionTypes": [],
 * "declaringClass": "java.lang.String",
 * "name": "equals",
 * "genericParameterTypes": "(java.lang.Object)",
 * "modifiers": 1,
 * "returnType": "boolean",
 * "parameterTypes": "(Object)",
 * "genericReturnType": "boolean"
 * }
 * </code>
 * <p/>
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class MethodInfo extends RoutineInfo implements IMethodInfo {
    /** Full Qualified Class Name that method return <code>java.lang.String</code> */
    private String genericReturnType;

    /** Short Class Name that method return <code>String</code> */
    private String returnType;

    public MethodInfo() {
    }

    public MethodInfo(Integer modifiers, String name, List<String> genericExceptionTypes, String genericParameterTypes,
                      String parameterTypes, String generic, String declaringClass, String genericReturnType, String returnType) {
        super(modifiers, name, genericExceptionTypes, genericParameterTypes, parameterTypes, generic, declaringClass);
        this.genericReturnType = genericReturnType;
        this.returnType = returnType;
    }

    /** {@inheritDoc} */
    @Override
    public String getGenericReturnType() {
        return genericReturnType;
    }

    /** {@inheritDoc} */
    @Override
    public void setGenericReturnType(String genericReturnType) {
        this.genericReturnType = genericReturnType;
    }

    /** {@inheritDoc} */
    @Override
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /** {@inheritDoc} */
    @Override
    public String getReturnType() {
        return returnType;
    }

}
