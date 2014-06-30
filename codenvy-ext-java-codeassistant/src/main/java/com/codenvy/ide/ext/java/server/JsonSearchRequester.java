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
import com.google.gson.JsonNull;
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
        constructor.add("signature", signature == null ? JsonNull.INSTANCE : new JsonPrimitive(new String(signature)));
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
