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

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.*;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo.ACLCapability;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo.QueryCapability;
import com.google.gwt.json.client.*;

import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JSONDeserializer.java 80595 2012-03-27 09:12:25Z azatsarynnyy $
 */
public abstract class JSONDeserializer<O> {
    // ----------- Common deserializers. -------------
    public static final JSONDeserializer<String> STRING_DESERIALIZER = new JSONDeserializer<String>() {
        @Override
        public String toObject(JSONValue json) {
            if (json == null) {
                return null;
            }
            JSONString jsonString = json.isString();
            if (jsonString == null) {
                return null;
            }
            return jsonString.stringValue();
        }

        @Override
        protected String[] createArray(int length) {
            return new String[length];
        }
    };

    public static final JSONDeserializer<Boolean> BOOLEAN_DESERIALIZER = new JSONDeserializer<Boolean>() {
        @Override
        public Boolean toObject(JSONValue json) {
            if (json == null) {
                return null;
            }
            JSONBoolean jsonBoolean = json.isBoolean();
            if (jsonBoolean == null) {
                return null;
            }
            return jsonBoolean.booleanValue();
        }

        @Override
        protected Boolean[] createArray(int length) {
            return new Boolean[length];
        }
    };


    // --------- Customized deserializers. -------------
    public static final JSONDeserializer<AccessControlEntry> ACL_DESERIALIZER =
            new JSONDeserializer<AccessControlEntry>() {
                @Override
                public AccessControlEntry toObject(JSONValue json) {
                    if (json == null) {
                        return null;
                    }
                    JSONObject jsonObject = json.isObject();
                    if (jsonObject == null) {
                        return null;
                    }
                    return new AccessControlEntry( //
                                                   STRING_DESERIALIZER.toObject(jsonObject.get("principal")), //
                                                   STRING_DESERIALIZER.toList(jsonObject.get("permissions")) //
                    );
                }

                @Override
                protected AccessControlEntry[] createArray(int length) {
                    return new AccessControlEntry[length];
                }
            };

    public static final JSONDeserializer<Property> PROPERTY_DESERIALIZER =
            new JSONDeserializer<Property>() {
                @Override
                public Property toObject(JSONValue json) {
                    if (json == null) {
                        return null;
                    }
                    JSONObject jsonObject = json.isObject();
                    if (jsonObject == null) {
                        return null;
                    }
                    JSONValue jsonValue = jsonObject.get("value");
                    if (jsonValue != null && jsonValue.isArray() != null) {
                        return new Property(STRING_DESERIALIZER.toObject(jsonObject.get("name")),
                                            STRING_DESERIALIZER.toList(jsonValue));
                    }
                    // Single String, null or some unexpected type.
                    return new Property(STRING_DESERIALIZER.toObject(jsonObject.get("name")),
                                        STRING_DESERIALIZER.toObject(jsonValue));
                }

                @Override
                protected Property[] createArray(int length) {
                    return new Property[length];
                }
            };


    public static final JSONDeserializer<Link> LINK_DESERIALIZER = new JSONDeserializer<Link>() {
        @Override
        public Link toObject(JSONValue json) {
            if (json == null) {
                return null;
            }
            JSONObject jsonObject = json.isObject();
            if (jsonObject == null) {
                return null;
            }
            return new Link( //
                             STRING_DESERIALIZER.toObject(jsonObject.get("href")), //
                             STRING_DESERIALIZER.toObject(jsonObject.get("rel")), //
                             STRING_DESERIALIZER.toObject(jsonObject.get("type")) //
            );
        }

        @Override
        protected Link[] createArray(int length) {
            return new Link[length];
        }
    };

//      public static final JSONDeserializer<LockToken> LOCK_TOKEN_DESERIALIZER = new JSONDeserializer<LockToken>()
//      {
//         @Override
//         public LockToken toObject(JSONValue json)
//         {
//            if (json == null)
//            {
//               return null;
//            }
//            JSONObject jsonObject = json.isObject();
//            if (jsonObject == null)
//            {
//               return null;
//            }
//            return new LockTokenBean(STRING_DESERIALIZER.toObject(jsonObject.get("token")));
//         }
//   
//         // not used, just to complete impl.
//         @Override
//         protected LockToken[] createArray(int length)
//         {
//            return new LockToken[length];
//         }
//      };

    public static final JSONDeserializer<VirtualFileSystemInfo> VFSINFO_DESERIALIZER =
            new JSONDeserializer<VirtualFileSystemInfo>() {
                @Override
                public VirtualFileSystemInfo toObject(JSONValue json) {
                    if (json == null) {
                        return null;
                    }
                    JSONObject jsonObject = json.isObject();

                    // TODO : root folder for VirtualFileSystemInfo
                    Folder rootFolder = new Folder(jsonObject.get("root").isObject());

                    return new VirtualFileSystemInfo(
                            STRING_DESERIALIZER.toObject(jsonObject.get("id")),
                            BOOLEAN_DESERIALIZER.toObject(jsonObject.get("versioningSupported")), //
                            BOOLEAN_DESERIALIZER.toObject(jsonObject.get("lockSupported")), //
                            STRING_DESERIALIZER.toObject(jsonObject.get("anonymousPrincipal")), //
                            STRING_DESERIALIZER.toObject(jsonObject.get("anyPrincipal")), //
                            // TODO:improve
                            STRING_DESERIALIZER.toList(jsonObject.get("permissions")), //
                            ACLCapability.fromValue(STRING_DESERIALIZER.toObject(jsonObject.get("aclCapability")).toLowerCase()), //
                            QueryCapability.fromValue(STRING_DESERIALIZER.toObject(jsonObject.get("queryCapability")).toLowerCase()),
                            LINK_DESERIALIZER.toMap(jsonObject.get("urlTemplates")), //
                            rootFolder //
                    );
                }

                // not used, just to complete impl.
                @Override
                protected VirtualFileSystemInfo[] createArray(int length) {
                    return new VirtualFileSystemInfo[length];
                }
            };

    // --------------------------------------

    /** @deprecated  */
    @Deprecated
    public O[] toArray(JSONValue json) {
        if (json == null) {
            return null;
        }
        JSONArray jsonArray = json.isArray();
        if (jsonArray == null) {
            return null;
        }
        int size = jsonArray.size();
        O[] array = createArray(size);
        for (int i = 0; i < size; i++) {
            array[i] = toObject(jsonArray.get(i));
        }
        return array;
    }

    protected abstract O[] createArray(int length);

    public JsonArray<O> toList(JSONValue json) {
        if (json == null) {
            return null;
        }
        JSONArray jsonArray = json.isArray();
        if (jsonArray == null) {
            return null;
        }
        int size = jsonArray.size();
        JsonArray<O> list = JsonCollections.createArray();
        for (int i = 0; i < size; i++) {
            list.add(toObject(jsonArray.get(i)));
        }
        return list;
    }

    public JsonStringMap<O> toMap(JSONValue json) {
        if (json == null) {
            return null;
        }
        JSONObject jsonObject = json.isObject();
        if (jsonObject == null) {
            return null;
        }
        // TODO switch from gwt.JSonObject to Jso from collide
        Set<String> keySet = jsonObject.keySet();
        JsonStringMap<O> map = JsonCollections.<O>createStringMap();
        for (Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
            String key = i.next();
            map.put(key, toObject(jsonObject.get(key)));
        }
        return map;
    }

    public abstract O toObject(JSONValue json);
}
