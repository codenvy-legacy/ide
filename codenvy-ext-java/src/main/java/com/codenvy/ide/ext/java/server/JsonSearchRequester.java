/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.eclipse.jdt.internal.codeassist.ISearchRequestor;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;

/**
 * @author Evgen Vidolob
 */
public class JsonSearchRequester implements ISearchRequestor {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    private JsonArray result = new JsonArray();

    @Override
    public void acceptConstructor(int modifiers, char[] simpleTypeName, int parameterCount, char[] signature, char[][] parameterTypes,
                                  char[][] parameterNames, int typeModifiers, char[] packageName, int extraFlags, String path,
                                  AccessRestriction access) {
        JsonObject constructor = new JsonObject();
        constructor.addProperty("modifiers", modifiers);
        constructor.addProperty("simpleTypeName", new String(simpleTypeName));
        constructor.addProperty("parameterCount", parameterCount);
        constructor.addProperty("signature", new String(signature));
        constructor.add("parameterTypes", BinaryTypeConvector.toJsonArrayString(parameterTypes));
        constructor.add("parameterNames", BinaryTypeConvector.toJsonArrayString(parameterNames));
        constructor.addProperty("typeModifiers", typeModifiers);
        constructor.addProperty("packageName", new String(packageName));
        constructor.addProperty("extraFlags", extraFlags);
        result.add(constructor);
    }

    public String toJsonString() {
        return gson.toJson(result);
    }

    @Override
    public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, int modifiers,
                           AccessRestriction accessRestriction) {
        JsonObject type = new JsonObject();
        type.addProperty("packageName", new String(packageName));
        type.addProperty("typeName", new String(typeName));
        type.add("enclosingTypeNames", BinaryTypeConvector.toJsonArrayString(enclosingTypeNames));
        type.addProperty("modifiers", modifiers);
        result.add(type);
    }

    @Override
    public void acceptPackage(char[] packageName) {
        result.add(new JsonPrimitive(new String(packageName)));
    }
}
