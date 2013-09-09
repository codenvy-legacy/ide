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
package org.exoplatform.ide.shell.client;

import com.google.gwt.storage.client.Storage;

import org.exoplatform.ide.vfs.shared.Folder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 26, 2011 evgen $
 */
public class Environment {
    private Storage storage;

    private static Environment instance;

    private Map<String, String> storageMap;

    private Folder currentFolder;

    /**
     *
     */
    protected Environment() {
        if (Storage.isSessionStorageSupported()) {
            storage = Storage.getLocalStorageIfSupported();
        } else {
            storageMap = new HashMap<String, String>();
        }
        instance = this;
    }

    /** @return instance of Environment class */
    public static Environment get() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public void saveValue(String key, String value) {
        if (storage != null) {
            storage.setItem(key, value);
        } else {
            storageMap.put(key, value);
        }
    }

    public String getValue(String key) {
        if (storage != null) {
            return storage.getItem(key);
        } else {
            return storageMap.get(key);
        }
    }

    /** @return the currentFolder */
    public Folder getCurrentFolder() {
        return currentFolder;
    }

    /**
     * @param currentFolder
     *         the currentFolder to set
     */
    public void setCurrentFolder(Folder currentFolder) {
        this.currentFolder = currentFolder;
    }

}
