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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.FolderImpl;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LinkImpl;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.LockTokenImpl;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
            if (json == null)
                return null;

            if (json.isNull() != null)
                return null;

            JSONString jsonString = json.isString();
            if (jsonString == null)
                return null;
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
            if (json == null)
                return null;
            JSONBoolean jsonBoolean = json.isBoolean();
            if (jsonBoolean == null)
                return null;
            return jsonBoolean.booleanValue();
        }

        @Override
        protected Boolean[] createArray(int length) {
            return new Boolean[length];
        }
    };

    public static final JSONDeserializer<Double> NUMBER_DESERIALIZER = new JSONDeserializer<Double>() {
        @Override
        public Double toObject(JSONValue json) {
            if (json == null)
                return null;
            JSONNumber jsonDouble = json.isNumber();
            if (jsonDouble == null)
                return null;
            return jsonDouble.doubleValue();
        }

        @Override
        protected Double[] createArray(int length) {
            return new Double[length];
        }
    };

    // --------- Customized deserializers. -------------
    public static final JSONDeserializer<AccessControlEntry> ACL_DESERIALIZER =
            new JSONDeserializer<AccessControlEntry>() {
                @Override
                public AccessControlEntry toObject(JSONValue json) {
                    if (json == null)
                        return null;
                    JSONObject jsonObject = json.isObject();
                    if (jsonObject == null)
                        return null;
                    return new AccessControlEntryImpl(PRINCIPAL_DESERIALIZER.toObject(jsonObject.get("principal")), //
                                                      STRING_DESERIALIZER.toSet(jsonObject.get("permissions")) //
                    );
                }

                @Override
                protected AccessControlEntry[] createArray(int length) {
                    return new AccessControlEntry[length];
                }
            };

    public static final JSONDeserializer<Principal> PRINCIPAL_DESERIALIZER =
            new JSONDeserializer<Principal>() {
                @Override
                public Principal toObject(JSONValue json) {
                    if (json == null)
                        return null;
                    JSONObject jsonObject = json.isObject();
                    if (jsonObject == null)
                        return null;
                    return new PrincipalImpl(STRING_DESERIALIZER.toObject(jsonObject.get("name")), //
                                             Principal.Type.valueOf(STRING_DESERIALIZER.toObject(jsonObject.get("type"))) //
                    );
                }

                @Override
                protected Principal[] createArray(int length) {
                    return new Principal[length];
                }
            };

    public static final JSONDeserializer<Property> STRING_PROPERTY_DESERIALIZER = new JSONDeserializer<Property>() {
        @Override
        public Property toObject(JSONValue json) {
            if (json == null)
                return null;
            JSONObject jsonObject = json.isObject();
            if (jsonObject == null)
                return null;
            JSONValue jsonValue = jsonObject.get("value");
            if (jsonValue != null && jsonValue.isArray() != null)
                return new PropertyImpl(STRING_DESERIALIZER.toObject(jsonObject.get("name")),
                                        STRING_DESERIALIZER.toList(jsonValue));
            // Single String, null or some unexpected type.
            return new PropertyImpl(STRING_DESERIALIZER.toObject(jsonObject.get("name")),
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
            if (json == null)
                return null;
            JSONObject jsonObject = json.isObject();
            if (jsonObject == null)
                return null;

            return new LinkImpl( //
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

    public static final JSONDeserializer<LockToken> LOCK_TOKEN_DESERIALIZER = new JSONDeserializer<LockToken>() {
        @Override
        public LockToken toObject(JSONValue json) {
            if (json == null)
                return null;
            JSONObject jsonObject = json.isObject();
            if (jsonObject == null)
                return null;
            return new LockTokenImpl(STRING_DESERIALIZER.toObject(jsonObject.get("token")));
        }

        // not used, just to complete impl.
        @Override
        protected LockToken[] createArray(int length) {
            return new LockToken[length];
        }
    };

    public static final JSONDeserializer<VirtualFileSystemInfo> VFSINFO_DESERIALIZER =
            new JSONDeserializer<VirtualFileSystemInfo>() {
                @SuppressWarnings({"unchecked", "rawtypes"})
                @Override
                public VirtualFileSystemInfo toObject(JSONValue json) {
                    if (json == null)
                        return null;
                    JSONObject jsonObject = json.isObject();

                    JSONObject root = jsonObject.get("root").isObject();
                    String vfsId = root.get("vfsId").isString().stringValue();
                    String rootId = root.get("id").isString().stringValue();
                    String rootName = root.get("name").isString().stringValue();
                    String rootMimeType = null;
                    if (root.get("mimeType").isString() != null)
                        rootMimeType = root.get("mimeType").isString().stringValue();
                    String rootPath = root.get("path").isString().stringValue();
                    long rootCreationDate = (long)root.get("creationDate").isNumber().doubleValue();
                    List properties = JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(root.get("properties"));
                    Map links = JSONDeserializer.LINK_DESERIALIZER.toMap(root.get("links"));

                    FolderImpl rootFolder =
                            new FolderImpl(vfsId, rootId, rootName, rootMimeType, rootPath, null, rootCreationDate, (List<Property>)properties,
                                           (Map<String, Link>)links);

                    return new VirtualFileSystemInfoImpl(
                            STRING_DESERIALIZER.toObject(jsonObject.get("id")),
                            BOOLEAN_DESERIALIZER.toObject(jsonObject.get("versioningSupported")), //
                            BOOLEAN_DESERIALIZER.toObject(jsonObject.get("lockSupported")), //
                            STRING_DESERIALIZER.toObject(jsonObject.get("anonymousPrincipal")), //
                            STRING_DESERIALIZER.toObject(jsonObject.get("anyPrincipal")), //
                            STRING_DESERIALIZER.toSet(jsonObject.get("permissions")), //
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
    public O[] toArray(JSONValue json) {
        if (json == null)
            return null;
        JSONArray jsonArray = json.isArray();
        if (jsonArray == null)
            return null;
        int size = jsonArray.size();
        O[] array = createArray(size);
        for (int i = 0; i < size; i++)
            array[i] = toObject(jsonArray.get(i));
        return array;
    }

    protected abstract O[] createArray(int length);

    public List<O> toList(JSONValue json) {
        if (json == null)
            return null;
        JSONArray jsonArray = json.isArray();
        if (jsonArray == null)
            return null;
        int size = jsonArray.size();
        List<O> list = createList(size);
        for (int i = 0; i < size; i++)
            list.add(toObject(jsonArray.get(i)));
        return list;
    }

    protected List<O> createList(int length) {
        return new ArrayList<O>(length);
    }

    public Set<O> toSet(JSONValue json) {
        if (json == null)
            return null;
        JSONArray jsonArray = json.isArray();
        if (jsonArray == null)
            return null;
        int size = jsonArray.size();
        Set<O> set = createSet(size);
        for (int i = 0; i < size; i++)
            set.add(toObject(jsonArray.get(i)));
        return set;
    }

    protected Set<O> createSet(int length) {
        return new HashSet<O>(length);
    }

    public Map<String, O> toMap(JSONValue json) {
        if (json == null)
            return null;
        JSONObject jsonObject = json.isObject();
        if (jsonObject == null)
            return null;
        Set<String> keySet = jsonObject.keySet();
        Map<String, O> map = createMap(keySet.size());
        for (Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
            String key = i.next();
            map.put(key, toObject(jsonObject.get(key)));
        }
        return map;
    }

    protected Map<String, O> createMap(int length) {
        return new HashMap<String, O>(length);
    }

    public abstract O toObject(JSONValue json);
}
