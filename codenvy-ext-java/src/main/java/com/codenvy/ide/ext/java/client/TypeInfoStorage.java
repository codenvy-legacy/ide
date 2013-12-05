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
package com.codenvy.ide.ext.java.client;

import com.codenvy.ide.ext.java.shared.TypeInfo;

import com.codenvy.ide.ext.java.client.core.IType;
import com.codenvy.ide.ext.java.client.core.Signature;
import com.codenvy.ide.ext.java.client.env.TypeImpl;

import com.codenvy.ide.collections.StringSet;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Local storage for Java types info. Key is FQN of Java Type. Value JSON representation of {@link TypeInfo} class
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 4:31:23 PM evgen $
 */
public class TypeInfoStorage {

    private static final String SHORT_TYPE_INFO = "__SHORT_TYPE_INFO__";

    private static TypeInfoStorage instance;

    private Storage storage;

    private StringSet packages;

    protected TypeInfoStorage() {
        storage = Storage.getSessionStorageIfSupported();
        if (storage == null) {
            //TODO session storage not supported
//         IDE.fireEvent(new OutputEvent("Your browser does not support 'Session Storage'", Type.WARNING));
        }
    }

    public static TypeInfoStorage get() {
        if (instance == null)
            instance = new TypeInfoStorage();
        return instance;
    }

    public void putType(String key, String type) {
        storage.setItem(key, type);
    }

    public String getType(String key) {
        return storage.getItem(key);
    }

    public boolean containsKey(String key) {
        return storage.getItem(key) != null;
    }

    public String getShortTypesInfo() {
        return storage.getItem(SHORT_TYPE_INFO);
    }

    public void setShortTypesInfo(String info) {
        storage.setItem(SHORT_TYPE_INFO, info);
    }

    public void removeTypeInfo(String key) {
        storage.removeItem(key);
    }

    public IType getTypeByFqn(String fqn) {
        String type = getType(fqn);
        JSONObject object = null;
        if (type == null) {
            String shortTypesInfo = TypeInfoStorage.get().getShortTypesInfo();
            if (shortTypesInfo == null)
                return null;
            JSONObject jsonObject = JSONParser.parseLenient(shortTypesInfo).isObject();
            if (jsonObject.containsKey("types")) {
                JSONArray array = jsonObject.get("types").isArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();
                    if (fqn.equals(obj.get("name").isString().stringValue())) {
                        object = obj;
                        break;
                    }
                }
            }
        } else {
            object = JSONParser.parseLenient(type).isObject();
        }
        if (object == null)
            return null;
        return new TypeImpl(object);

    }

    public List<JSONObject> getTypesByNamePrefix(String prefix, boolean fqnPart) {
        List<JSONObject> res = new ArrayList<JSONObject>();
        for (int i = 0; i < storage.getLength(); i++) {
            String key = storage.key(i);
            if (fqnPart && !key.startsWith(prefix)) {
                continue;
            } else {
                String simpleName = Signature.getSimpleName(key);
                if (simpleName.equals(key) || !simpleName.startsWith(prefix))
                    continue;
            }
            res.add(JSONParser.parseLenient(storage.getItem(key)).isObject());
        }
        return res;

    }

    /** Remove all items from storage */
    public void clear() {
        storage.clear();
    }

    /**
     * @param projectId
     * @param packages
     */
    public void setPackages(String projectId, StringSet packages) {
        this.packages = packages;
    }

    public StringSet getPackages(String projectId) {
        return packages;
    }
}
