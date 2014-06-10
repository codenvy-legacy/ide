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

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

/**
 * @author Evgen Vidolob
 */
public class TypeBindingConvetror {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    public static String toJsonBinaryType(SourceTypeBinding binding) {
        JsonObject object = new JsonObject();
        object.add("annotations", toJsonAnnotations(binding.getAnnotations()));
        object.add("enclosingMethod", null);
        object.add("enclosingTypeName", binding.enclosingType() == null ? JsonNull.INSTANCE : new JsonPrimitive(
                new String(binding.enclosingType().constantPoolName())));

        object.add("fields", toJsonFields(binding.fields()));
        object.add("genericSignature",
                   binding.genericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(binding.genericSignature())));
        object.add("interfaceNames", toJsonInterfaces(binding.superInterfaces()));
        object.add("memberTypes", toJsonMemberTypes(binding.memberTypes()));
        object.add("methods", toJsonMethods(binding.methods()));
        object.add("missingTypeNames", null);
        object.add("name",
                   binding.constantPoolName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(binding.constantPoolName())));
        object.add("sourceName", binding.sourceName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(binding.sourceName())));
        object.add("superclassName",
                   binding.superclass() == null ? JsonNull.INSTANCE
                                                : new JsonPrimitive(new String(binding.superclass().constantPoolName())));
        object.add("tagBits", new JsonPrimitive(String.valueOf(binding.tagBits)));
        object.add("anonymous", new JsonPrimitive(binding.isAnonymousType()));
        object.add("local", new JsonPrimitive(binding.isLocalType()));
        object.add("member", new JsonPrimitive(binding.isMemberType()));
        object.add("sourceFileName",
                   binding.sourceName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(binding.sourceName())));
        object.add("modifiers", new JsonPrimitive(binding.modifiers));
        object.add("binaryType", new JsonPrimitive(true));
        object.add("fileName", null);
        return gson.toJson(object);
    }

    private static JsonElement toJsonMethods(MethodBinding[] methods) {
        if (methods == null) return JsonNull.INSTANCE;
        JsonArray jsonElements = new JsonArray();
        for (MethodBinding method : methods) {
            jsonElements.add(toJsonMethod(method));
        }
        return jsonElements;
    }

    private static JsonElement toJsonMethod(MethodBinding method) {
        JsonObject object = new JsonObject();
        object.addProperty("modifiers", method.modifiers);
        object.addProperty("constructor", method.isConstructor());
        object.add("argumentNames", toJsonParametersName(method.sourceMethod()));
        object.add("annotations", toJsonAnnotations(method.getAnnotations()));
        object.add("defaultValue", toJsonDefaultValue(method.getDefaultValue()));
        object.add("exceptionTypeNames", toJsonExceptionTypeNames(method.thrownExceptions));
        object.add("genericSignature",
                   method.genericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.genericSignature())));
        object.add("methodDescriptor",
                   method.signature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.signature())));
        object.add("parameterAnnotations", toJsonParameterAnnotations(method));
        object.add("selector", method.selector == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(method.selector)));
        object.addProperty("tagBits", String.valueOf(method.tagBits));
        object.addProperty("clinit", false);
        return object;
    }

    private static JsonElement toJsonParameterAnnotations(MethodBinding method) {
        AnnotationBinding[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (AnnotationBinding[] parameterAnnotation : parameterAnnotations) {
            array.add(toJsonAnnotations(parameterAnnotation));
        }
        return array;
    }

    private static JsonElement toJsonExceptionTypeNames(ReferenceBinding[] thrownExceptions) {
        if (thrownExceptions == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (ReferenceBinding exception : thrownExceptions) {
            array.add(new JsonPrimitive(new String(exception.constantPoolName())));
        }
        return array;
    }

    private static JsonElement toJsonDefaultValue(Object defaultValue) {
        if (defaultValue == null) return JsonNull.INSTANCE;
        if(defaultValue == null) return JsonNull.INSTANCE;
        JsonObject object = new JsonObject();
        if(defaultValue instanceof Constant){
            object.add("constant", BinaryTypeConvector.toJsonConstant((Constant)defaultValue));
        } else if(defaultValue instanceof TypeBinding) {
            object.addProperty("class", new String(((TypeBinding)defaultValue).constantPoolName()));
        } else if(defaultValue instanceof AnnotationBinding){
            object.add("annotation", toJsonAnnotation((AnnotationBinding)defaultValue));
        }else if(defaultValue instanceof FieldBinding){
            FieldBinding signature = (FieldBinding)defaultValue;
            JsonObject enumSignature = new JsonObject();
            enumSignature.addProperty("typeName", new String(signature.type.signature()));
            enumSignature.addProperty("constantName", new String(signature.name));
            object.add("enum", enumSignature);
        }else
        if(defaultValue instanceof Object[]) {
            JsonArray array = new JsonArray();
            for (Object o : (Object[])defaultValue) {
                array.add(toJsonDefaultValue(o));
            }
            object.add("array", array);
        }

        return object;
    }

    private static JsonElement toJsonParametersName(AbstractMethodDeclaration parameters) {
        if (parameters==null || parameters.arguments == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();

        for (Argument parameter : parameters.arguments) {
            array.add(new JsonPrimitive(new String(parameter.binding.name)));
        }
        return array;

    }

    private static JsonElement toJsonMemberTypes(ReferenceBinding[] memberTypes) {
        if (memberTypes == null) return null;
        JsonArray array = new JsonArray();
        for (ReferenceBinding type : memberTypes) {
            array.add(toJsonMemberType(type));
        }
        return array;

    }

    private static JsonElement toJsonMemberType(ReferenceBinding type) {
        JsonObject object = new JsonObject();
        object.add("enclosingTypeName",
                   type.enclosingType() == null ? JsonNull.INSTANCE
                                                : new JsonPrimitive(new String(type.enclosingType().constantPoolName())));
        object.addProperty("modifiers", type.modifiers);
        object.add("name", type.constantPoolName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(type.constantPoolName())));
        return object;
    }

    private static JsonElement toJsonInterfaces(ReferenceBinding[] interfaces) {
        if (interfaces == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (ReferenceBinding anInterface : interfaces) {
            array.add(new JsonPrimitive(new String(anInterface.constantPoolName())));
        }
        return array;
    }

    private static JsonElement toJsonFields(FieldBinding[] fields) {
        if(fields == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (FieldBinding field : fields) {
            array.add(toJsonField(field));
        }
        return array;
    }

    private static JsonElement toJsonField(FieldBinding field) {
        JsonObject object = new JsonObject();
        object.addProperty("modifiers", field.modifiers);
        object.add("constant", BinaryTypeConvector.toJsonConstant(field.constant()));
        object.add("genericSignature",
                   field.genericSignature() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.genericSignature())));
        object.add("name", field.name == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.name)));
        object.addProperty("tagBits", String.valueOf(field.tagBits));
        object.add("typeName", field.type == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(field.type.signature())));
        object.add("annotations", toJsonAnnotations(field.getAnnotations()));
        return object;
    }

    private static JsonElement toJsonAnnotations(AnnotationBinding[] annotations) {
        if(annotations == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (AnnotationBinding annotation : annotations) {
            array.add(toJsonAnnotation(annotation));
        }
        return array;
    }

    private static JsonElement toJsonAnnotation(AnnotationBinding annotation) {
        JsonObject object = new JsonObject();
        object.add("typeName",
                   annotation.getAnnotationType() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(annotation.getAnnotationType().constantPoolName())));
        object.add("elementValuePairs", toJsonElementValuePairs(annotation.getElementValuePairs()));
        return object;
    }

    private static JsonElement toJsonElementValuePairs(ElementValuePair[] elementValuePairs) {
        if(elementValuePairs == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (ElementValuePair pair : elementValuePairs) {
            array.add(toJsonElementValuePair(pair));
        }
        return array;
    }

    private static JsonElement toJsonElementValuePair(ElementValuePair pair) {
        JsonObject object = new JsonObject();
        object.add("name", pair.getName() == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(pair.getName())));
        object.add("value", toJsonDefaultValue(pair.getValue()));
        return object;
    }
}
