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
package org.exoplatform.ide.security.paas;

import com.codenvy.ide.commons.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DummyCredentialStore implements CredentialStore {
    private final Map<String, Pair[]> myCredentials = new HashMap<String, Pair[]>();
    private final Lock                lock          = new ReentrantLock();

    @Override
    public boolean load(String user, String target, Credential credential) throws CredentialStoreException {
        lock.lock();
        try {
            Pair[] persistentCredential = myCredentials.get(user + target);
            if (persistentCredential == null) {
                return false;
            }
            for (Pair attribute : persistentCredential) {
                credential.setAttribute(attribute.getName(), attribute.getValue());
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void save(String user, String target, Credential credential) throws CredentialStoreException {
        lock.lock();
        try {
            final Map<String, String> attributes = credential.getAttributes();
            Pair[] persistentCredential = new Pair[attributes.size()];
            int i = 0;
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                persistentCredential[i++] = new Pair(e.getKey(), e.getValue());
            }
            myCredentials.put(user + target, persistentCredential);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public boolean delete(String user, String target) throws CredentialStoreException {
        lock.lock();
        try {
            return myCredentials.remove(user + target) != null;
        } finally {
            lock.unlock();
        }
    }
}
