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
package org.exoplatform.ide.editor.java.client;

import org.exoplatform.ide.codeassistant.jvm.shared.*;
import org.exoplatform.ide.editor.api.codeassitant.*;
import org.exoplatform.ide.editor.codeassistant.ModifierHelper;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;

import java.util.*;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class JavaCodeAssistantUtils {

    /**
     * Convert TypesList object to List<Token>
     *
     * @param types
     * @return
     */
    // TODO this methods need temporary and maybe
    //removed in future after rewriting codeAssitant API
    public static List<Token> types2tokens(TypesList types) {
        if (types != null) {
            List<Token> tokens = new ArrayList<Token>(types.getTypes().size());
            for (ShortTypeInfo typeInfo : types.getTypes()) {
                String fqn = typeInfo.getName();
                String name = fqn.substring(fqn.lastIndexOf(".") + 1);
                String type = typeInfo.getType();
                int modifiers = typeInfo.getModifiers();
                Token token = new TokenImpl(name, TokenType.valueOf(type));
                token.setProperty(TokenProperties.FQN, new StringProperty(fqn));
                token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifiers));
                tokens.add(token);
            }
            return tokens;
        }
        return Collections.emptyList();
    }

    /**
     * Convert Type object to JavaClass
     *
     * @param types
     * @return
     */
    // TODO this methods need temporary and maybe
    //removed in future after rewriting codeAssitant API
    public static JavaClass type2javaClass(TypeInfo type) {
        JavaClass classInfo = new JavaClass();
        classInfo.getPublicFields().addAll(getPublicFields(type.getFields()));
        classInfo.getPublicMethods().addAll(getPublicMethods(type.getMethods()));
        classInfo.getAbstractMethods().addAll(getAbstractMethods(type.getMethods()));
        classInfo.getPublicConstructors().addAll(getPublicConstructors(type.getMethods()));
        return classInfo;
    }

    /**
     * @param list
     * @return
     */
    private static Collection<? extends Token> getAbstractMethods(List<MethodInfo> list) {
        //TODO filter same methods
        Map<String, Token> methods = new HashMap<String, Token>();
        if (list != null)
            for (MethodInfo mi : list) {
                int modifier = mi.getModifiers();
                if (ModifierHelper.isAbstract(modifier)) {
                    Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
                    token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                    token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                    token.setProperty(TokenProperties.GENERIC_RETURN_TYPE, new StringProperty(mi.getReturnType()));
                    token.setProperty(TokenProperties.PARAMETER_TYPES,
                                      new StringProperty(array2string(mi.getParameterTypes())));
                    token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES,
                                      new StringProperty(array2string(mi.getExceptionTypes())));
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
    private static List<? extends Token> getPublicMethods(List<MethodInfo> list) {
        List<Token> methods = new ArrayList<Token>();
        if (list != null)
            for (MethodInfo mi : list) {
                int modifier = (int)mi.getModifiers();
                if (ModifierHelper.isPublic(modifier)) {
                    Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
                    token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                    token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                    token.setProperty(TokenProperties.RETURN_TYPE, new StringProperty(mi.getReturnType()));
                    token.setProperty(TokenProperties.PARAMETER_TYPES,
                                      new StringProperty(array2string(mi.getParameterTypes())));
                    token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES,
                                      new StringProperty(array2string(mi.getExceptionTypes())));
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
    private static List<? extends Token> getPublicFields(List<FieldInfo> list) {
        List<Token> fields = new ArrayList<Token>();
        if (list != null)
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
    private static List<? extends Token> getPublicConstructors(List<MethodInfo> list) {
        List<Token> constructors = new ArrayList<Token>();
        if (list != null)
            for (MethodInfo mi : list) {
                int modifier = mi.getModifiers();
                if (!ModifierHelper.isInterface(modifier)) {
                    String name = mi.getName();
                    name = name.substring(name.lastIndexOf('.') + 1);
                    Token token = new TokenImpl(name, TokenType.CONSTRUCTOR);
                    token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
                    token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
                    token.setProperty(TokenProperties.PARAMETER_TYPES,
                                      new StringProperty(array2string(mi.getParameterTypes())));
                    token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES,
                                      new StringProperty(array2string(mi.getExceptionTypes())));
                    constructors.add(token);
                }
            }
        return constructors;
    }

    private static String array2string(List<String> a) {
        if (a == null)
            return "null";
        int iMax = a.size() - 1;
        if (iMax == -1)
            return "()";

        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; ; i++) {
            String fqnParam = a.get(i);
            b.append(String.valueOf(fqnParam.substring(fqnParam.lastIndexOf(".") + 1)));
            if (i == iMax)
                return b.append(')').toString();
            b.append(", ");
        }
    }

}
