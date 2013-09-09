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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.editor.api.codeassitant.*;
import org.exoplatform.ide.editor.codeassistant.ModifierHelper;

import java.util.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:00:56 PM evgen $
 * @see Unmarshallable Created by The eXo Platform SAS.
 */
public class ClassDescriptionUnmarshaller implements Unmarshallable<JavaClass> {

    private JavaClass classInfo;

    interface MyFactory extends AutoBeanFactory {
        AutoBean<TypeInfo> typeInfo();
    }

    /** @param classInfo */
    public ClassDescriptionUnmarshaller(JavaClass classInfo) {
        this.classInfo = classInfo;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            if (response.getStatusCode() != HTTPStatus.NO_CONTENT) {
                MyFactory myFactory = GWT.create(MyFactory.class);
                AutoBean<TypeInfo> bean = AutoBeanCodex.decode(myFactory, TypeInfo.class, response.getText());
                TypeInfo info = bean.as();
                toJavaClass(info);
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse class description");
        }
    }

    private void toJavaClass(TypeInfo info) {
        classInfo.getPublicFields().addAll(getPublicFields(info.getFields()));
        classInfo.getPublicMethods().addAll(getPublicMethods(info.getMethods()));
        classInfo.getAbstractMethods().addAll(getAbstractMethods(info.getMethods()));
        classInfo.getPublicConstructors().addAll(getPublicConstructors(info.getMethods()));
    }

    /**
     * @param list
     * @return
     */
    private Collection<? extends Token> getAbstractMethods(List<MethodInfo> list) {
        // TODO filter same methods
        Map<String, Token> methods = new HashMap<String, Token>();
        for (MethodInfo mi : list) {
            int modifier = mi.getModifiers();
            if (ModifierHelper.isAbstract(modifier)) {
                Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
                token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                token.setProperty(TokenProperties.GENERIC_RETURN_TYPE, new StringProperty(mi.getReturnType()));
                token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
                        .getParameterTypes().toArray() : null));
                token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
                                                                                             ? mi.getExceptionTypes().toArray() : null));
                methods.put(mi.getName() + mi.getParameterTypes().toArray().toString(), token);
            }
        }
        return methods.values();
    }

    /**
     * Get all public methods
     *
     * @param list
     * @return {@link List} of {@link TokenExt} that contains all public method of class
     */
    private List<? extends Token> getPublicMethods(List<MethodInfo> list) {
        List<Token> methods = new ArrayList<Token>();
        for (MethodInfo mi : list) {
            int modifier = (int)mi.getModifiers();
            if (ModifierHelper.isPublic(modifier)) {
                Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
                token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                token.setProperty(TokenProperties.RETURN_TYPE, new StringProperty(mi.getReturnType()));
                token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
                        .getParameterTypes().toArray() : null));
                token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
                                                                                             ? mi.getExceptionTypes().toArray() : null));
                methods.add(token);
            }
        }
        return methods;
    }

    /**
     * Get all public fields
     *
     * @param list
     * @return {@link List} of {@link TokenExt} that represent public fields of Class
     */
    private List<? extends Token> getPublicFields(List<FieldInfo> list) {
        List<Token> fields = new ArrayList<Token>();
        for (FieldInfo fi : list) {
            int modifier = fi.getModifiers();
            if (ModifierHelper.isPublic(modifier)) {
                Token token = new TokenImpl(fi.getName(), TokenType.FIELD);
                token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(fi.getDeclaringClass()));
                token.setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(fi.getType()));
                fields.add(token);
            }
        }
        return fields;
    }

    /**
     * Get all public constructors
     *
     * @param list
     * @return {@link List} of {@link TokenExt} that represent Class constructors
     */
    private List<? extends Token> getPublicConstructors(List<MethodInfo> list) {
        List<Token> constructors = new ArrayList<Token>();
        for (MethodInfo mi : list) {
            int modifier = mi.getModifiers();
            if (!ModifierHelper.isInterface(modifier)) {
                String name = mi.getName();
                name = name.substring(name.lastIndexOf('.') + 1);
                Token token = new TokenImpl(name, TokenType.CONSTRUCTOR);
                token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
                        .getParameterTypes().toArray() : null));
                token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
                                                                                             ? mi.getExceptionTypes().toArray() : null));
                constructors.add(token);
            }
        }
        return constructors;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public JavaClass getPayload() {
        return classInfo;
    }

}
