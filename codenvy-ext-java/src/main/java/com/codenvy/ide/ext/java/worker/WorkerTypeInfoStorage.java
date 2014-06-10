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
