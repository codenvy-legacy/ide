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
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RoutineInfo extends Member implements IRoutineInfo {
    /** Array FQN of exceptions throws by method */
    private List<String> genericExceptionTypes;

    /**
     * Full Qualified Class Name of parameter <code>(java.lang.Object)</code>
     * (not use for now)
     */
    private String genericParameterTypes;

    /** Short Class name of Parameter <code>(Object)</code> */
    private String parameterTypes;

    /**
     * Method declaration like:
     * <code>public boolean java.lang.String.equals(java.lang.Object)</code>
     */
    private String generic;

    /**
     * Full Qualified Class Name where method declared Example: method equals()
     * declared in java.lang.String
     */
    private String declaringClass;


    public RoutineInfo() {
    }

    public RoutineInfo(Integer modifiers, String name, List<String> genericExceptionTypes, String genericParameterTypes,
                       String parameterTypes, String generic, String declaringClass) {
        super(modifiers, name);
        this.genericExceptionTypes = genericExceptionTypes;
        this.genericParameterTypes = genericParameterTypes;
        this.parameterTypes = parameterTypes;
        this.generic = generic;
        this.declaringClass = declaringClass;
    }

    /** {@inheritDoc} */
    @Override
    public String getGenericParameterTypes() {
        return genericParameterTypes;
    }

    /** {@inheritDoc} */
    @Override
    public void setGenericParameterTypes(String genericParameterTypes) {
        this.genericParameterTypes = genericParameterTypes;
    }

    /** {@inheritDoc} */
    @Override
    public void setGeneric(String generic) {
        this.generic = generic;
    }

    /** {@inheritDoc} */
    @Override
    public String getGeneric() {
        return generic;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeclaringClass() {
        return declaringClass;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getGenericExceptionTypes() {
        return genericExceptionTypes;
    }

    /** {@inheritDoc} */
    @Override
    public void setGenericExceptionTypes(List<String> genericExceptionTypes) {
        this.genericExceptionTypes = genericExceptionTypes;
    }

    /** {@inheritDoc} */
    @Override
    public String getParameterTypes() {
        return parameterTypes;
    }

    /** {@inheritDoc} */
    @Override
    public void setParameterTypes(String parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

}
