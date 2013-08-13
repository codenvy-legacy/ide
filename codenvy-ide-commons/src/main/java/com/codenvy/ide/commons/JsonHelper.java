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
package com.codenvy.ide.commons;

import org.everrest.core.impl.provider.json.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonHelper {
    @SuppressWarnings("unchecked")
    public static <O> String toJson(O instance) {
        try {
            JsonValue json;
            if (instance.getClass().isArray()) {
                json = JsonGenerator.createJsonArray(instance);
            } else if (instance instanceof Collection) {
                json = JsonGenerator.createJsonArray((Collection<?>)instance);
            } else if (instance instanceof Map) {
                json = JsonGenerator.createJsonObjectFromMap((Map<String, ?>)instance);
            } else {
                json = JsonGenerator.createJsonObject(instance);
            }

            Writer w = new StringWriter();
            json.writeTo(new JsonWriter(w));
            return w.toString();
        } catch (JsonException jsone) {
            // Must not happen since serialize well known object.
            throw new RuntimeException(jsone.getMessage(), jsone);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <O> O fromJson(String json, Class<O> klass, Type type) throws ParsingResponseException {
        return fromJson(parseJson(json), klass, type);
    }

    public static <O> O fromJson(InputStream json, Class<O> klass, Type type) throws ParsingResponseException {
        return fromJson(parseJson(json), klass, type);
    }

    public static <O> O fromJson(Reader json, Class<O> klass, Type type) throws ParsingResponseException {
        return fromJson(parseJson(json), klass, type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <O> O fromJson(JsonValue jsonValue, Class<O> klass, Type type) throws ParsingResponseException {
        try {
            O instance;
            if (klass.isArray()) {
                instance = (O)ObjectBuilder.createArray(klass, jsonValue);
            } else if (Collection.class.isAssignableFrom(klass)) {
                Class k = klass;
                instance = (O)ObjectBuilder.createCollection(k, type, jsonValue);
            } else if (Map.class.isAssignableFrom(klass)) {
                Class k = klass;
                instance = (O)ObjectBuilder.createObject(k, type, jsonValue);
            } else {
                instance = ObjectBuilder.createObject(klass, jsonValue);
            }
            return instance;
        } catch (JsonException jsone) {
            throw new ParsingResponseException(jsone.getMessage(), jsone);
        }
    }

    public static JsonValue parseJson(String json) throws ParsingResponseException {
        return parseJson(new StringReader(json));
    }

    public static JsonValue parseJson(InputStream json) throws ParsingResponseException {
        return parseJson(new InputStreamReader(json, Charset.forName("UTF-8")));
    }

    public static JsonValue parseJson(Reader json) throws ParsingResponseException {
        try {
            JsonParser parser = new JsonParser();
            parser.parse(json);
            return parser.getJsonObject();
        } catch (JsonException jsone) {
            throw new ParsingResponseException(jsone.getMessage(), jsone);
        }
    }
}