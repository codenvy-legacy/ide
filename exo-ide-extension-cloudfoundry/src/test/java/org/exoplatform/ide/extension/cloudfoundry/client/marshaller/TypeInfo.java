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
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TypeInfo extends ShortTypeInfo implements ITypeInfo {
    private List<IMethodInfo> methods;

    private List<IMethodInfo> declaredMethods;

    private List<IRoutineInfo> constructors;

    private List<IRoutineInfo> declaredConstructors;

    private List<IFieldInfo> fields;

    private List<IFieldInfo> declaredFields;

    private String superClass;

    private List<String> interfaces;

    public TypeInfo() {
    }

    public TypeInfo(Integer modifiers, String name, List<IMethodInfo> methods, List<IMethodInfo> declaredMethods,
                    List<IRoutineInfo> constructors, List<IRoutineInfo> declaredConstructors, List<IFieldInfo> fields,
                    List<IFieldInfo> declaredFields,
                    String superClass, List<String> interfaces, String qualifiedName, String type) {
        super(modifiers, name, qualifiedName, type);
        this.methods = methods;
        this.declaredMethods = declaredMethods;
        this.constructors = constructors;
        this.declaredConstructors = declaredConstructors;
        this.fields = fields;
        this.declaredFields = declaredFields;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    /** {@inheritDoc} */
    @Override
    public List<IMethodInfo> getMethods() {
        return methods;
    }

    /** {@inheritDoc} */
    @Override
    public void setMethods(List<IMethodInfo> methods) {
        this.methods = methods;
    }

    /** {@inheritDoc} */
    @Override
    public List<IMethodInfo> getDeclaredMethods() {
        return declaredMethods;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaredMethods(List<IMethodInfo> declaredMethods) {
        this.declaredMethods = declaredMethods;
    }

    /** {@inheritDoc} */
    @Override
    public List<IRoutineInfo> getConstructors() {
        return constructors;
    }

    /** {@inheritDoc} */
    @Override
    public void setConstructors(List<IRoutineInfo> constructors) {
        this.constructors = constructors;
    }

    /** {@inheritDoc} */
    @Override
    public List<IRoutineInfo> getDeclaredConstructors() {
        return declaredConstructors;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaredConstructors(List<IRoutineInfo> declaredConstructors) {
        this.declaredConstructors = declaredConstructors;
    }

    /** {@inheritDoc} */
    @Override
    public List<IFieldInfo> getFields() {
        return fields;
    }

    /** {@inheritDoc} */
    @Override
    public void setFields(List<IFieldInfo> fields) {
        this.fields = fields;
    }

    /** {@inheritDoc} */
    @Override
    public List<IFieldInfo> getDeclaredFields() {
        return declaredFields;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaredFields(List<IFieldInfo> declaredFields) {
        this.declaredFields = declaredFields;
    }

    /** {@inheritDoc} */
    @Override
    public String getSuperClass() {
        return superClass;
    }

    /** {@inheritDoc} */
    @Override
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getInterfaces() {
        return interfaces;
    }

    /** {@inheritDoc} */
    @Override
    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }


}
