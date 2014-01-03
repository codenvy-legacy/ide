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
package com.codenvy.ide.ext.java.server.parser;


import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.java.server.CodeAssistantException;
import com.codenvy.ide.ext.java.server.CodeAssistantStorage;
import com.codenvy.ide.ext.java.shared.FieldInfo;
import com.codenvy.ide.ext.java.shared.JavaType;
import com.codenvy.ide.ext.java.shared.Member;
import com.codenvy.ide.ext.java.shared.MethodInfo;
import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypeInfo;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.model.WildcardType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** @author Evgen Vidolob */
public class JavaTypeToTypeInfoConverter {

    public enum Modifier {
        STATIC(0x00000008),
        FINAL(0x00000010),
        PRIVATE(0x00000002),
        PUBLIC(0x00000001),
        PROTECTED(0x00000004),
        ABSTRACT(
                0x00000400),
        STRICTFP(0x00000800),
        SYNCHRONIZED(0x00000020),
        THREADSAFE(0),
        TRANSIENT(0x00000080),
        VOLATILE(
                0x00000040),
        NATIVE(0x00000100);
        private final int mod;

        Modifier(int i) {
            this.mod = i;
        }

        public int value() {
            return mod;
        }
    }

    private static final int AccInterface = 0x0200;

    private static final int AccEnum = 0x4000;

    private static final Logger LOG = LoggerFactory.getLogger(JavaTypeToTypeInfoConverter.class);

    private CodeAssistantStorage storage;

    private final Set<String> dependency;

    /** @param storage */
    public JavaTypeToTypeInfoConverter(CodeAssistantStorage storage, Set<String> dependency) {
        super();
        this.storage = storage;
        this.dependency = dependency;
    }

    public TypeInfo convert(JavaClass clazz) {
        TypeInfo type = DtoFactory.getInstance().createDto(TypeInfo.class);
        type.setName(clazz.getFullyQualifiedName());
        type.setType(getType(clazz).name());
        if (clazz.getSuperJavaClass() != null)
            type.setSuperClass(clazz.getSuperJavaClass().getFullyQualifiedName());
        else
            type.setSuperClass("java.lang.Object");

        type.setModifiers(typeModifierToInt(clazz));
        type.setInterfaces(toListFqn(clazz.getImplements()));
        type.setFields(toFieldInfo(clazz));
        JavaMethod[] methods = clazz.getMethods();
        type.setMethods(toMethods(clazz, methods));
        type.setSignature(createTypeSignature(clazz));
        type.setNestedTypes(getNestedTypes(clazz));
        return type;
    }

    /**
     * @param clazz
     * @return
     */
    private List<Member> getNestedTypes(JavaClass clazz) {
        if (clazz.getNestedClasses().length != 0) {
            List<Member> members = new ArrayList<Member>();
            for (JavaClass nested : clazz.getNestedClasses()) {
                Member member = DtoFactory.getInstance().createDto(Member.class);
                member.setName(nested.getFullyQualifiedName());
                member.setModifiers(typeModifierToInt(nested));
                members.add(member);
            }
            return members;
        }

        return null;
    }

    /**
     * @param clazz
     * @return
     */
    private String createTypeSignature(JavaClass clazz) {
        StringBuilder signature = new StringBuilder();
        boolean isClassGeneric = false;
        if (clazz.getTypeParameters().length != 0) {
            isClassGeneric = true;
            signature.append(createTypeParameters(clazz.getTypeParameters()));
        }
        if (isClassGeneric) {
            appendSuperClassAndInterfaces(clazz, signature);
        } else {
            boolean isInterfacesGeneric = false;
            for (Type t : clazz.getImplements()) {
                if (t.getActualTypeArguments() != null) {
                    isInterfacesGeneric = true;
                    break;
                }
            }

            if (isInterfacesGeneric
                || (clazz.getSuperClass() != null && clazz.getSuperClass().getActualTypeArguments() != null))
                appendSuperClassAndInterfaces(clazz, signature);
        }
        return signature.length() == 0 ? null : signature.toString();
    }

    /** @param typeVariables */
    private String createTypeParameters(TypeVariable[] typeVariables) {
        StringBuilder signature = new StringBuilder("<");

        for (TypeVariable var : typeVariables) {
            signature.append(var.getName());

            Type[] bounds = getParameterBounds(var);
            if (bounds != null) {
                Type type = bounds[0];
                if (type.isResolved())
                    if (type.getJavaClass().isInterface())
                        signature.append(':');
                    else {
                        try {
                            TypeInfo typeInfo = storage.getTypeByFqn(type.getJavaClass().getFullyQualifiedName(), dependency);
                            if (typeInfo != null && JavaType.valueOf(typeInfo.getType()) == JavaType.INTERFACE)
                                signature.append(':');
                        } catch (CodeAssistantException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                signature.append(':').append(createSignatureForType(type));
                for (int i = 1; i < bounds.length; i++) {
                    signature.append(':').append(createSignatureForType(bounds[i]));
                }
            } else
                signature.append(':').append("Ljava/lang/Object;");
        }
        return signature.append('>').toString();
    }

    /**
     * @param var
     * @return
     */
    private static Type[] getParameterBounds(TypeVariable var) {
        try {
            Field boundsField = TypeVariable.class.getDeclaredField("bounds");
            boundsField.setAccessible(true);
            Type[] bounds = (Type[])boundsField.get(var);
            return bounds;
        } catch (Exception e) {
            LOG.error("Can't read bounds for TypeVariable -" + var.getFullyQualifiedName(), e);
            return null;
        }

    }

    /**
     * @param clazz
     * @param signature
     */
    private static void appendSuperClassAndInterfaces(JavaClass clazz, StringBuilder signature) {
        signature.append(createSignatureForType(clazz.getSuperClass()));
        for (Type t : clazz.getImplements()) {
            signature.append(createSignatureForType(t));
        }

    }

    /** @return  */
    private static String createSignatureForType(Type type) {
        StringBuilder signature = new StringBuilder();
        if (type == null) {
            signature.append("Ljava/lang/Object;");
            return signature.toString();
        }

        if (type instanceof WildcardType)
            signature.append(getWildcards((WildcardType)type));
        if (type.isArray()) {
            for (int i = 0; i < type.getDimensions(); i++)
                signature.append('[');
        }
        //indus style :(, but with qdox we cant determine if type is generics
        if (type.getFullyQualifiedName().length() == 1) {
            signature.append("T").append(type.getGenericValue()).append(';');
        } else
            signature.append(SignatureCreator.createByteCodeTypeSignature(type.getFullyQualifiedName()));
        if (type.getActualTypeArguments() != null) {
            // remove trailing ';'
            signature.setLength(signature.length() - 1);
            signature.append('<');
            for (Type t : type.getActualTypeArguments()) {
                if (t.getActualTypeArguments() != null)

                    signature.append(createSignatureForType(t));
                else {
                    if (t.getFullyQualifiedName().contains(".")) {
                        if (t instanceof WildcardType) {
                            signature.append(getWildcards((WildcardType)t));
                        }
                        if (t.isArray()) {
                            for (int i = 0; i < t.getDimensions(); i++)
                                signature.append('[');
                        }
                        signature.append(SignatureCreator.createByteCodeTypeSignature(t.getFullyQualifiedName()));
                    } else {
                        if (t.getFullyQualifiedName().equals("?")) {
                            signature.append('*');
                        } else {
                            if (t instanceof WildcardType) {
                                signature.append(getWildcards((WildcardType)t));
                            }
                            if (t.isArray()) {
                                for (int i = 0; i < t.getDimensions(); i++)
                                    signature.append('[');
                            }
                            signature.append('T').append(t.getFullyQualifiedName()).append(';');
                        }
                    }
                }
            }
            signature.append(">;");
        }
        return signature.toString();
    }

    /** @return  */
    private static char getWildcards(WildcardType wildcardType) {
        try {
            Field field = WildcardType.class.getDeclaredField("wildcardExpressionType");
            field.setAccessible(true);
            String value = (String)field.get(wildcardType);
            if ("extends".equals(value))
                return '+';
            else if ("super".equals(value))
                return '-';
            return 0;
        } catch (Exception e) {
            LOG.error("Can't read wildcardExpressionType in type " + wildcardType.getFullyQualifiedName(), e);
            return 0;
        }

    }

    public static JavaType getType(JavaClass clazz) {
        if (clazz.isInterface()) {
            return JavaType.INTERFACE;
        }
        if (clazz.isEnum())
            return JavaType.ENUM;
        try {
            Field field = clazz.getClass().getDeclaredField("isAnnotation");
            field.setAccessible(true);
            if ((Boolean)field.get(clazz)) {
                return JavaType.ANNOTATION;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return JavaType.CLASS;
    }

    /**
     * @param methods
     * @return
     */
    private List<MethodInfo> toMethods(JavaClass clazz, JavaMethod[] methods) {
        List<MethodInfo> con = new ArrayList<MethodInfo>();
        boolean hasConstructor = false;
        for (JavaMethod m : methods) {
            MethodInfo info = DtoFactory.getInstance().createDto(MethodInfo.class);
            info.setExceptionTypes(toListFqn(m.getExceptions()));
            info.setModifiers(clazz.isInterface() ? Modifier.ABSTRACT.value() : modifiersToInteger(m.getModifiers()));
            Type[] parameterTypes = m.getParameterTypes(true);
            info.setParameterTypes(toParameters(parameterTypes));
            info.setParameterNames(toParametersName(m.getParameters()));
            info.setName(m.getName());
            info.setDeclaringClass(m.getParentClass().getFullyQualifiedName());
            info.setDescriptor(SignatureCreator.createMethodSignature(m));
            info.setSignature(createMethodSignature(m, clazz));
            if (!m.isConstructor()) {
                String returnType = m.getReturnType().getFullyQualifiedName();
                info.setReturnType(returnType);
                info.setConstructor(false);
            } else {
                info.setConstructor(true);
                hasConstructor = true;
            }
            con.add(info);
        }
        // if class don't has a constructor - add default
        if (!hasConstructor && !clazz.isInterface()) {
            MethodInfo defaultConstructor = DtoFactory.getInstance().createDto(MethodInfo.class);
            defaultConstructor.setDeclaringClass(clazz.getFullyQualifiedName());
            defaultConstructor.setDescriptor("()V;");
            defaultConstructor.setModifiers(Modifier.PUBLIC.value());
            defaultConstructor.setConstructor(true);
            con.add(defaultConstructor);
        }
        return con;
    }

    /**
     * @param m
     * @param clazz
     * @return
     */
    private String createMethodSignature(JavaMethod m, JavaClass clazz) {
        boolean isMethodGeneric = false;
        StringBuilder signature = new StringBuilder();
        if (m.getTypeParameters() != null && m.getTypeParameters().length != 0) {
            isMethodGeneric = true;
            signature.append(createTypeParameters(m.getTypeParameters()));
        }
        signature.append('(');
        for (JavaParameter parameter : m.getParameters()) {
            if (parameter.getType().getActualTypeArguments() != null)
                isMethodGeneric = true;
            signature.append(createSignatureForType(parameter.getType()));
        }
        signature.append(')');
        if (m.isConstructor()) {
            signature.append('V');
        } else {
            Type returnType = m.getReturnType();
            if (!returnType.isPrimitive() && !returnType.getFullyQualifiedName().contains("."))
                signature.append('T').append(returnType.getFullyQualifiedName()).append(';');
            else
                signature.append(createSignatureForType(m.getReturnType()));
        }
        return isMethodGeneric ? signature.toString() : null;
    }

    /**
     * @param parameters
     * @return
     */
    private static List<String> toParametersName(JavaParameter[] parameters) {
        List<String> paramsNames = new ArrayList<String>(parameters.length);
        for (JavaParameter p : parameters) {
            paramsNames.add(p.getName());
        }
        return paramsNames;
    }

    /**
     * @param parameterTypes
     * @return
     */
    public static List<String> toParameters(Type[] parameterTypes) {
        List<String> params = new ArrayList<String>();
        for (Type type : parameterTypes) {
            params.add(type.getFullyQualifiedName());
        }
        return params;
    }

    /** @return  */
    private static List<FieldInfo> toFieldInfo(JavaClass clazz) {
        JavaField[] fields = clazz.getFields();
        List<FieldInfo> fi = new ArrayList<FieldInfo>();
        boolean isGeneric = false;
        Set<String> parameters = null;
        if (clazz.getTypeParameters().length != 0) {
            isGeneric = true;
            parameters = new HashSet<>(clazz.getTypeParameters().length);
            for (TypeVariable v : clazz.getTypeParameters())
                parameters.add(v.getName());
        }
        for (int i = 0; i < fields.length; i++) {
            FieldInfo info = DtoFactory.getInstance().createDto(FieldInfo.class);
            JavaField f = fields[i];
            info.setDeclaringClass(f.getParentClass().getFullyQualifiedName());
            info.setType(f.getType().getValue());
            info.setName(f.getName());
            info.setModifiers(modifiersToInteger(f.getModifiers()));
            info.setDescriptor(SignatureCreator.createTypeSignature(f).replaceAll("\\.", "/"));
            if (f.getType().isPrimitive())
                info.setValue(f.getInitializationExpression());

            if (isGeneric && parameters.contains(f.getType().getFullyQualifiedName())) {
                StringBuilder signature = new StringBuilder();
                if (f.getType().isArray())
                    signature.append('[');
                signature.append('T').append(f.getType().getFullyQualifiedName()).append(';');
                info.setSignature(signature.toString());
            } else if (f.getType().getActualTypeArguments() != null) {
                info.setSignature(createSignatureForType(f.getType()));
            }
            fi.add(info);
        }

        return fi;
    }

    /**
     * @param modifiers
     * @return
     */
    private static Integer modifiersToInteger(String[] modifiers) {
        int i = 0;

        for (String s : modifiers) {
            i = i | Modifier.valueOf(s.toUpperCase()).value();
        }

        return i;
    }

    /** @return  */
    private static Integer typeModifierToInt(JavaClass type) {
        int i = modifiersToInteger(type.getModifiers());
        if (type.isInterface())
            i |= AccInterface;
        else if (type.isEnum())
            i |= AccEnum;
        return i;
    }

    /**
     * @param types
     * @return
     */
    private static List<String> toListFqn(Type[] types) {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < types.length; i++) {
            arr.add(types[i].getFullyQualifiedName());
        }
        return arr;
    }

    /**
     * @param clazz
     * @return
     */
    public ShortTypeInfo toShortTypeInfo(JavaClass clazz) {
        ShortTypeInfo info = DtoFactory.getInstance().createDto(ShortTypeInfo.class);
        info.setModifiers(typeModifierToInt(clazz));
        info.setName(clazz.getFullyQualifiedName());
        info.setType(getType(clazz).name());
        info.setSignature(createTypeSignature(clazz));
        return info;
    }

    /**
     * @param tags
     * @return
     */
    public static String tagsToString(DocletTag[] tags) {
        if (tags == null)
            return "";
        StringBuilder b = new StringBuilder();
        for (DocletTag t : tags) {
            b.append("<p>").append("<b>").append(t.getName()).append("</b>").append("<br/>").append(t.getValue())
             .append("</p>");
        }
        return b.toString();
    }
}
