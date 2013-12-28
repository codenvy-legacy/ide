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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.model.AccessControlEntry;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JSONSerializer.java 75889 2011-11-01 10:42:51Z anya $
 */
public abstract class JSONSerializer<O> {
    // --------- Common serializers. -------------
    public static final JSONSerializer<String> STRING_SERIALIZER = new JSONSerializer<String>() {
        @Override
        public JSONValue fromObject(String object) {
            if (object == null) {
                return JSONNull.getInstance();
            }
            return new JSONString(object);
        }
    };

    public static final JSONSerializer<Double> NUMBER_SERIALIZER = new JSONSerializer<Double>() {
        @Override
        public JSONValue fromObject(Double object) {
            if (object == null) {
                return JSONNull.getInstance();
            }
            return new JSONNumber(object.doubleValue());
        }
    };

    public static final JSONSerializer<Boolean> BOLEAN_SERIALIZER = new JSONSerializer<Boolean>() {
        @Override
        public JSONValue fromObject(Boolean object) {
            if (object == null) {
                return JSONNull.getInstance();
            }
            return JSONBoolean.getInstance(object.booleanValue());
        }
    };

    // --------- Customized serializers. -------------
    public static final JSONSerializer<Property> PROPERTY_SERIALIZER = new JSONSerializer<Property>() {
        @Override
        public JSONValue fromObject(Property source) {
            if (source == null) {
                return JSONNull.getInstance();
            }
            JSONObject target = new JSONObject();
            target.put("name", STRING_SERIALIZER.fromObject(source.getName()));
            target.put("value", STRING_SERIALIZER.fromCollection(source.getValue()));
            return target;
        }
    };

    public static final JSONSerializer<AccessControlEntry> ACL_SERIALIZER = new JSONSerializer<AccessControlEntry>() {
        @Override
        public JSONValue fromObject(AccessControlEntry source) {
            if (source == null) {
                return JSONNull.getInstance();
            }
            JSONObject target = new JSONObject();
            target.put("principal", STRING_SERIALIZER.fromObject(source.getPrincipal()));
            target.put("permissions", STRING_SERIALIZER.fromCollection(source.getPermissions()));
            return target;
        }
    };

    public JSONValue fromArray(O[] source) {
        if (source == null) {
            return JSONNull.getInstance();
        }
        JSONArray target = new JSONArray();
        for (int i = 0; i < source.length; i++) {
            target.set(i, fromObject(source[i]));
        }
        return target;
    }

    public JSONValue fromCollection(Array<O> source) {
        if (source == null) {
            return JSONNull.getInstance();
        }
        JSONArray target = new JSONArray();
        for (int i = 0; i < source.size(); i++) {
            target.set(i, fromObject(source.get(i)));
        }
        return target;
    }

    public JSONValue fromMap(StringMap<O> source) {
        if (source == null) {
            return JSONNull.getInstance();
        }
        JSONObject target = new JSONObject();

        Array<String> keys = source.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            target.put(keys.get(i), fromObject(source.get(keys.get(i))));
        }
        return target;
    }

    public abstract JSONValue fromObject(O source);
}
