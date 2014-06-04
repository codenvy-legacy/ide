/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.compiler.env.ClassSignature;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.impl.Constant;

import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_JavaLangString;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_boolean;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_byte;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_char;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_double;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_float;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_int;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_long;
import static org.eclipse.jdt.internal.compiler.lookup.TypeIds.T_short;

/**
 * @author Evgen Vidolob
 */
public class BinaryTypeConvector {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    public static String toJsonBinaryType(IBinaryType type) {
        JsonObject object = new JsonObject();
        object.add("annotations", toJsonAnnotations(type.getAnnotations()));
        object.add("enclosingMethod",
                   type.getEnclosingMethod() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getEnclosingMethod())));
        object.add("enclosingTypeName",
                   type.getEnclosingTypeName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getEnclosingTypeName())));
        object.add("fields", toJsonFields(type.getFields()));
        object.add("genericSignature",
                   type.getGenericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getGenericSignature())));
        object.add("interfaceNames", toJsonArrayString(type.getInterfaceNames()));
        object.add("memberTypes", toJsonMemberTypes(type.getMemberTypes()));
        object.add("methods", toJsonMethods(type.getMethods()));
        object.add("missingTypeNames", toJsonMissingTypeNames(type.getMissingTypeNames()));
        object.add("name", type.getName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getName())));
        object.add("sourceName", type.getSourceName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getSourceName())));
        object.add("superclassName",
                   type.getSuperclassName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getSuperclassName())));
        object.add("tagBits", new JsonPrimitive(String.valueOf(type.getTagBits())));
        object.add("anonymous", new JsonPrimitive(type.isAnonymous()));
        object.add("local", new JsonPrimitive(type.isLocal()));
        object.add("member", new JsonPrimitive(type.isMember()));
        object.add("sourceFileName",
                   type.sourceFileName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.sourceFileName())));
        object.add("modifiers", new JsonPrimitive(type.getModifiers()));
        object.add("binaryType", new JsonPrimitive(type.isBinaryType()));
        object.add("fileName", type.getFileName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getFileName())));
        return gson.toJson(object);
    }


    private static JsonElement toJsonMethods(IBinaryMethod[] methods) {
        if (methods == null) return JsonNull.INSTANCE;
        JsonArray jsonElements = new JsonArray();
        for (IBinaryMethod method : methods) {
            jsonElements.add(toJsonMethod(method));
        }
        return jsonElements;
    }

    private static JsonElement toJsonMethod(IBinaryMethod method) {
        JsonObject object = new JsonObject();
        object.addProperty("modifiers", method.getModifiers());
        object.addProperty("constructor", method.isConstructor());
        object.add("argumentNames", toJsonArrayString(method.getArgumentNames()));
        object.add("annotations", toJsonAnnotations(method.getAnnotations()));
        object.add("defaultValue", toJsonDefaultValue(method.getDefaultValue()));
        object.add("exceptionTypeNames", toJsonArrayString(method.getExceptionTypeNames()));
        object.add("genericSignature",
                   method.getGenericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.getGenericSignature())));
        object.add("methodDescriptor",
                   method.getMethodDescriptor() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.getMethodDescriptor())));
        object.add("parameterAnnotations", toJsonParameterAnnotations(method));
        object.add("selector", method.getSelector() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.getSelector())));
        object.addProperty("tagBits", String.valueOf(method.getTagBits()));
        object.addProperty("clinit", method.isClinit());
        return object;
    }

    private static JsonElement toJsonParameterAnnotations(IBinaryMethod method) {
        if (method.getAnnotatedParametersCount() != 0) {
            JsonArray array = new JsonArray();
            int parameterCount = Signature.getParameterCount(method.getMethodDescriptor());
            for (int i = 0; i < parameterCount; i++) {
                array.add(toJsonAnnotations(method.getParameterAnnotations(i)));
            }
            return array;
        } else return JsonNull.INSTANCE;
    }

    private static JsonElement toJsonFields(IBinaryField[] fields) {
        if (fields == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (IBinaryField field : fields) {
            array.add(toJsonField(field));
        }
        return array;
    }

    private static JsonElement toJsonField(IBinaryField field) {
        JsonObject object = new JsonObject();
        object.addProperty("modifiers", field.getModifiers());
        object.add("constant", toJsonConstant(field.getConstant()));
        object.add("genericSignature",
                   field.getGenericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.getGenericSignature())));
        object.add("name", field.getName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.getName())));
        object.addProperty("tagBits", String.valueOf(field.getTagBits()));
        object.add("typeName", field.getTypeName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.getTypeName())));
        object.add("annotations", toJsonAnnotations(field.getAnnotations()));
        return object;
    }

    public static JsonElement toJsonConstant(Constant constant) {
        if (constant == null) return JsonNull.INSTANCE;
        JsonObject con = new JsonObject();
        con.addProperty("typeId", constant.typeID());
        JsonElement val;
        switch (constant.typeID()) {
            case T_int:
                val = new JsonPrimitive(constant.intValue());
                break;
            case T_byte:
                val = new JsonPrimitive(constant.byteValue());
                break;
            case T_short:
                val = new JsonPrimitive(constant.shortValue());
                break;
            case T_char:
                val = new JsonPrimitive(constant.charValue());
                break;
            case T_float:
                val = new JsonPrimitive(String.valueOf(constant.floatValue()));
                break;
            case T_double:
                if (Constant.NotAConstant.equals(constant)) {
                    val = new JsonPrimitive("NaN");
                    con.addProperty("NotAConstant", 1);
                } else {
                    val = new JsonPrimitive(constant.doubleValue());
                }
                break;
            case T_boolean:
                val = new JsonPrimitive(constant.booleanValue());
                break;
            case T_long:
                val = new JsonPrimitive(String.valueOf(constant.longValue()));
                break;
            case T_JavaLangString:
                val = new JsonPrimitive(constant.stringValue());
                break;
            default:
                val = JsonNull.INSTANCE;
        }
        con.add("value", val);
        return con;
    }

    private static JsonElement toJsonAnnotations(IBinaryAnnotation[] annotations) {
        if (annotations == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (IBinaryAnnotation annotation : annotations) {
            array.add(toJsonAnnotation(annotation));
        }
        return array;
    }

    private static JsonElement toJsonAnnotation(IBinaryAnnotation annotation) {
        JsonObject object = new JsonObject();
        object.add("typeName",
                   annotation.getTypeName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(annotation.getTypeName())));
        object.add("elementValuePairs", toJsonElementValuePairs(annotation.getElementValuePairs()));
        return object;
    }

    private static JsonElement toJsonElementValuePairs(IBinaryElementValuePair[] elementValuePairs) {
        if (elementValuePairs == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (IBinaryElementValuePair pair : elementValuePairs) {
            array.add(toJsonElementValuePair(pair));
        }
        return array;
    }

    private static JsonElement toJsonElementValuePair(IBinaryElementValuePair pair) {
        JsonObject object = new JsonObject();
        object.add("name", pair.getName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(pair.getName())));
        object.add("value", toJsonDefaultValue(pair.getValue()));
        return object;
    }

    private static JsonElement toJsonDefaultValue(Object defaultValue) {
        if (defaultValue == null) return JsonNull.INSTANCE;
        JsonObject object = new JsonObject();
        if (defaultValue instanceof Constant) {
            object.add("constant", toJsonConstant((Constant)defaultValue));
        } else if (defaultValue instanceof ClassSignature) {
            object.addProperty("class", new String(((ClassSignature)defaultValue).getTypeName()));
        } else if (defaultValue instanceof IBinaryAnnotation) {
            object.add("annotation", toJsonAnnotation((IBinaryAnnotation)defaultValue));
        } else if (defaultValue instanceof EnumConstantSignature) {
            EnumConstantSignature signature = (EnumConstantSignature)defaultValue;
            JsonObject enumSignature = new JsonObject();
            enumSignature.addProperty("typeName", new String(signature.getTypeName()));
            enumSignature.addProperty("constantName", new String(signature.getEnumConstantName()));
            object.add("enum", enumSignature);
        } else if (defaultValue instanceof Object[]) {
            JsonArray array = new JsonArray();
            for (Object o : (Object[])defaultValue) {
                array.add(toJsonDefaultValue(o));
            }
            object.add("array", array);
        }

        return object;
    }

    private static JsonElement toJsonMemberTypes(IBinaryNestedType[] memberTypes) {
        if (memberTypes == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (IBinaryNestedType type : memberTypes) {
            array.add(toJsonMemberType(type));
        }
        return array;
    }

    private static JsonElement toJsonMemberType(IBinaryNestedType type) {
        JsonObject object = new JsonObject();
        object.add("enclosingTypeName",
                   type.getEnclosingTypeName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getEnclosingTypeName())));
        object.addProperty("modifiers", type.getModifiers());
        object.add("name", type.getName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.getName())));
        return object;
    }

    private static JsonElement toJsonMissingTypeNames(char[][][] missingTypeNames) {
        if (missingTypeNames == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (char[][] typeName : missingTypeNames) {
            array.add(toJsonArrayString(typeName));
        }
        return array;
    }


    public static JsonElement toJsonArrayString(char[][] chars) {
        if (chars == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (char[] aChar : chars) {
            array.add(new JsonPrimitive(new String(aChar)));
        }
        return array;
    }


}
