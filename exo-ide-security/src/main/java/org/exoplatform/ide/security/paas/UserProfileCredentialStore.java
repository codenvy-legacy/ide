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

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.commons.lang.cache.Cache;
import com.codenvy.commons.lang.cache.SLRUCache;
import com.codenvy.ide.commons.Pair;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.exception.UserExistenceException;
import com.codenvy.organization.model.User;


import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UserProfileCredentialStore implements CredentialStore {
    /** Prefix for attribute of user profile that store PaaS credential. */
    private static final String PaaS_CREDENTIAL_ATTRIBUTE_PREFIX = "paas.credential.";
    private final UserManager userManager;
    // protected with lock
    private final Cache<String, Pair[]> cache = new SLRUCache<String, Pair[]>(50, 100);
    private final Lock                  lock  = new ReentrantLock();

    public UserProfileCredentialStore(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public boolean load(String user, String target, Credential credential) throws CredentialStoreException {
        lock.lock();
        try {
            final String key = cacheKey(user, target);
            Pair[] persistentCredential = cache.get(key);
            if (persistentCredential == null) {
                final User myUser;
                try {
                    myUser = userManager.getUserByAlias(user);
                } catch (UserExistenceException e) {
                    // user not found
                    return false;
                }
                final String credentialAttribute = myUser.getProfile().getAttribute(credentialAttributeName(target));
                if (credentialAttribute == null) {
                    return false;
                }
                persistentCredential = JsonHelper.fromJson(credentialAttribute, Pair[].class, null);
                cache.put(key, persistentCredential);
            }
            for (Pair attribute : persistentCredential) {
                credential.setAttribute(attribute.getName(), attribute.getValue());
            }

            return true;
        } catch (OrganizationServiceException e) {
            throw new CredentialStoreException(
                    String.format("Failed to load credential for '%s' and target '%s'. ", user, target), e);
        } catch (JsonParseException e) {
            throw new CredentialStoreException(
                    String.format("Failed to parse credential for '%s' and target '%s'. ", user, target), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void save(String user, String target, Credential credential) throws CredentialStoreException {
        lock.lock();
        try {
            final String key = cacheKey(user, target);
            cache.remove(key);
            final User myUser;
            try {
                myUser = userManager.getUserByAlias(user);
            } catch (UserExistenceException e) {
                throw new CredentialStoreException(String.format("Unknown user '%s'. ", user));
            }
            final Map<String, String> attributes = credential.getAttributes();
            Pair[] persistentCredential = new Pair[attributes.size()];
            int i = 0;
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                persistentCredential[i++] = new Pair(e.getKey(), e.getValue());
            }
            myUser.getProfile().setAttribute(credentialAttributeName(target), JsonHelper.toJson(persistentCredential));
            userManager.updateUser(myUser);
            cache.put(key, persistentCredential);
        } catch (OrganizationServiceException e) {
            throw new CredentialStoreException(
                    String.format("Failed to save credential for '%s' and target '%s'. ", user, target), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean delete(String user, String target) throws CredentialStoreException {
        lock.lock();
        try {
            final String key = cacheKey(user, target);
            cache.remove(key);
            final User myUser;
            try {
                myUser = userManager.getUserByAlias(user);
            } catch (UserExistenceException e) {
                // Ignore non existent users.
                return false;
            }
            myUser.getProfile().removeAttribute(credentialAttributeName(target));
            userManager.updateUser(myUser);
            return true;
        } catch (OrganizationServiceException e) {
            throw new CredentialStoreException(
                    String.format("Failed to delete credential for '%s' and target '%s'. ", user, target), e);
        } finally {
            lock.unlock();
        }
    }

    private String cacheKey(String user, String target) {
        return user + ':' + target;
    }

    private String credentialAttributeName(String target) {
        return PaaS_CREDENTIAL_ATTRIBUTE_PREFIX + target;
    }
}
