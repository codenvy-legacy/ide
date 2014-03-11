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
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.core.IType;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.search.Type;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryType;
import com.codenvy.ide.ext.java.worker.env.BinaryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkerTypeInfoStorage {

    private static WorkerTypeInfoStorage instance;

    private Map<String, IBinaryType> storage = new HashMap<String, IBinaryType>();
    private String shortTypesInfo;

    public static WorkerTypeInfoStorage get() {
        if (instance == null) {
            instance = new WorkerTypeInfoStorage();
        }
        return instance;
    }

    public void putType(String key, IBinaryType type) {
        storage.put(key, type);
    }

    public IBinaryType getType(String key) {
        return storage.get(key);
    }

    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }
//
//    public String getShortTypesInfo() {
//        return storage.getItem(SHORT_TYPE_INFO);
//    }
//
//    public void setShortTypesInfo(String info) {
//        storage.setItem(SHORT_TYPE_INFO, info);
//    }

    public List<IBinaryType> getTypesByNamePrefix(String prefix, boolean fqnPart) {
        List<IBinaryType> res = new ArrayList<IBinaryType>();
        for (String key : storage.keySet()) {
            if (fqnPart && !key.startsWith(prefix)) {
                continue;
            } else {
                String simpleName = Signature.getSimpleName(key);
                if (simpleName.equals(key) || !simpleName.startsWith(prefix))
                    continue;
            }
            res.add(storage.get(key));
        }
        return res;
    }

    public IType getTypeByFqn(String fqn) {
        BinaryType type = (BinaryType)getType(fqn);
        return type != null ? new Type(type) : null;
    }

    public void setShortTypesInfo(String shortTypesInfo) {
        this.shortTypesInfo = shortTypesInfo;
    }

    public String getShortTypesInfo() {
        return shortTypesInfo;
    }

    public void removeFqn(String fqn) {
        JsoArray<String> fqnToRemove = JsoArray.create();
        for (String key : storage.keySet()) {
            if (key.startsWith(fqn)) {
                fqnToRemove.add(key);
            }
        }

        for (String key : fqnToRemove.asIterable()) {
            storage.remove(key);
        }
    }
}
