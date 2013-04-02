/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.commons;

import org.everrest.core.impl.provider.json.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * Tool to serialize/deserialize Java objects to/from JSON representation.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see JsonNameConventions
 */
public class JsonHelper {
    @SuppressWarnings("unchecked")
    public static <O> String toJson(O instance) {
        return toJson(instance, JsonNameConventions.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public static <O> String toJson(O instance, JsonNameConvention nameConvention) {
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
            json.writeTo(new NameConventionJsonWriter(w, nameConvention));
            return w.toString();
        } catch (JsonException jsone) {
            // Must not happen since serialize well known object.
            throw new RuntimeException(jsone.getMessage(), jsone);
        }
    }

    public static <O> O fromJson(String json, Class<O> klass, Type type) throws JsonParseException {
        return fromJson(parseJson(json), klass, type);
    }

    public static <O> O fromJson(String json, Class<O> klass, Type type, JsonNameConvention nameConvention) throws JsonParseException {
        return fromJson(parseJson(json, nameConvention), klass, type);
    }


    public static <O> O fromJson(InputStream json, Class<O> klass, Type type) throws JsonParseException {
        return fromJson(parseJson(json), klass, type);
    }

    public static <O> O fromJson(InputStream json, Class<O> klass, Type type, JsonNameConvention nameConvention) throws JsonParseException {
        return fromJson(parseJson(json, nameConvention), klass, type);
    }


    public static <O> O fromJson(Reader json, Class<O> klass, Type type) throws JsonParseException {
        return fromJson(parseJson(json), klass, type);
    }

    public static <O> O fromJson(Reader json, Class<O> klass, Type type, JsonNameConvention nameConvention) throws JsonParseException {
        return fromJson(parseJson(json, nameConvention), klass, type);
    }


    private static <O> O fromJson(JsonValue jsonValue, Class<O> klass, Type type) throws JsonParseException {
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
            throw new JsonParseException(jsone.getMessage(), jsone);
        }
    }

    public static JsonValue parseJson(String json) throws JsonParseException {
        return parseJson(new StringReader(json));
    }

    public static JsonValue parseJson(String json, JsonNameConvention nameConvention) throws JsonParseException {
        return parseJson(new StringReader(json), nameConvention);
    }


    public static JsonValue parseJson(InputStream json) throws JsonParseException {
        return parseJson(new InputStreamReader(json, Charset.forName("UTF-8")));
    }

    public static JsonValue parseJson(InputStream json, JsonNameConvention nameConvention) throws JsonParseException {
        return parseJson(new InputStreamReader(json, Charset.forName("UTF-8")), nameConvention);
    }


    public static JsonValue parseJson(Reader json) throws JsonParseException {
        return parseJson(json, JsonNameConventions.DEFAULT);
    }

    public static JsonValue parseJson(Reader json, JsonNameConvention nameConvention) throws JsonParseException {
        try {
            JsonParser parser = new NameConventionJsonParser(nameConvention);
            parser.parse(json);
            return parser.getJsonObject();
        } catch (JsonException jsone) {
            throw new JsonParseException(jsone.getMessage(), jsone);
        }
    }
}
