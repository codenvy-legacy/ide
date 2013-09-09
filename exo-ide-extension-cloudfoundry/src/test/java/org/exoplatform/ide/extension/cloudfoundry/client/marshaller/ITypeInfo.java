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
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface ITypeInfo extends IShortType {

    /** @return the methods */
    List<IMethodInfo> getMethods();

    /**
     * @param methods
     *         the methods to set
     */
    void setMethods(List<IMethodInfo> methods);

    /** @return the declaredMethods */
    List<IMethodInfo> getDeclaredMethods();

    /**
     * @param declaredMethods
     *         the declaredMethods to set
     */
    void setDeclaredMethods(List<IMethodInfo> declaredMethods);

    /** @return the constructors */
    List<IRoutineInfo> getConstructors();

    /**
     * @param constructors
     *         the constructors to set
     */
    void setConstructors(List<IRoutineInfo> constructors);

    /** @return the declaredConstructors */
    List<IRoutineInfo> getDeclaredConstructors();

    /**
     * @param declaredConstructors
     *         the declaredConstructors to set
     */
    void setDeclaredConstructors(List<IRoutineInfo> declaredConstructors);

    /** @return the fields */
    List<IFieldInfo> getFields();

    /**
     * @param fields
     *         the fields to set
     */
    void setFields(List<IFieldInfo> fields);

    /** @return the declaredFields */
    List<IFieldInfo> getDeclaredFields();

    /**
     * @param declaredFields
     *         the declaredFields to set
     */
    void setDeclaredFields(List<IFieldInfo> declaredFields);

    /** @return the superClass */
    String getSuperClass();

    /**
     * @param superClass
     *         the superClass to set
     */
    void setSuperClass(String superClass);

    /** @return the interfaces */
    List<String> getInterfaces();

    /**
     * @param interfaces
     *         the interfaces to set
     */
    void setInterfaces(List<String> interfaces);

}