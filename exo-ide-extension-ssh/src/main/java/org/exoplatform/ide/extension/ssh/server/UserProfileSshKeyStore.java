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
package org.exoplatform.ide.extension.ssh.server;

import com.codenvy.ide.commons.cache.Cache;
import com.codenvy.ide.commons.cache.SLRUCache;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.User;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.exoplatform.services.security.ConversationState;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UserProfileSshKeyStore implements SshKeyStore {
    private static final int            PRIVATE                      = 0;
    private static final int            PUBLIC                       = 1;
    private static final String         KEY_ATTRIBUTE_PREFIX         = "ssh.key.";
    /** Prefix for attribute of user profile that store private SSH key. */
    private static final String         PRIVATE_KEY_ATTRIBUTE_PREFIX = KEY_ATTRIBUTE_PREFIX + "private.";
    /** Prefix for attribute of user profile that store public SSH key. */
    private static final String         PUBLIC_KEY_ATTRIBUTE_PREFIX  = KEY_ATTRIBUTE_PREFIX + "public.";

    private final UserManager           userManager;
    // protected with lock
    private final Cache<String, SshKey> cache                        = new SLRUCache<String, SshKey>(50, 100);
    private final Lock                  lock                         = new ReentrantLock();
    private final JSch                  genJsch;

    public UserProfileSshKeyStore(UserManager userManager) {
        this.userManager = userManager;
        this.genJsch = new JSch();
    }

    @Override
    public void addPrivateKey(String host, byte[] key) throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            final String cacheKey = cacheKey(userId, host, PRIVATE);
            cache.remove(cacheKey);
            final User myUser = userManager.getUserByAlias(userId);
            final String sshKeyAttributeName = sshKeyAttributeName(host, PRIVATE);
            if (myUser.getProfile().getAttribute(sshKeyAttributeName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            final String keyAsString = new String(key); // Expected keys already encoded with Base64
            myUser.getProfile().setAttribute(sshKeyAttributeName, keyAsString);
            userManager.updateUser(myUser);
            cache.put(cacheKey, new SshKey(cacheKey, key));
        } catch (OrganizationServiceException e) {
            throw new SshKeyStoreException(String.format("Failed to add private key. '%s'", e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SshKey getPrivateKey(String host) throws SshKeyStoreException {
        return getKey(host, PRIVATE);
    }

    @Override
    public SshKey getPublicKey(String host) throws SshKeyStoreException {
        return getKey(host, PUBLIC);
    }

    private SshKey getKey(String host, int i) throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            final String cacheKey = cacheKey(userId, host, i);
            SshKey key = cache.get(cacheKey);
            if (key == null) {
                final User myUser = userManager.getUserByAlias(userId);
                String keyAsString = myUser.getProfile().getAttribute(sshKeyAttributeName(host, i));
                if (keyAsString == null) {
                    // Try to find key for parent domain. This is required for openshift integration but may be useful for others also.
                    final String attributePrefix = i == PRIVATE ? PRIVATE_KEY_ATTRIBUTE_PREFIX : PUBLIC_KEY_ATTRIBUTE_PREFIX;
                    for (Iterator<Map.Entry<String, String>> iterator = myUser.getProfile().getAttributes().entrySet().iterator(); iterator.hasNext()
                                                                                                                                   && keyAsString == null;) {
                        Map.Entry<String, String> entry = iterator.next();
                        String attributeName = entry.getKey();
                        if (attributeName.startsWith(attributePrefix)) {
                            // Lets say we found attribute 'ssh.key.private.codenvy.com'
                            // and we are looking for key for host 'my-site.codenvy.com'.
                            // 1. Get domain name - remove prefix 'ssh.key.private.'
                            // 2. We found the key if host name ends with name we got above.
                            if (host.endsWith(attributeName.substring(attributePrefix.length()))) {
                                keyAsString = entry.getValue();
                            }
                        }
                    }
                }
                if (keyAsString != null) {
                    key = new SshKey(cacheKey, keyAsString.getBytes());
                    cache.put(cacheKey, key);
                }
            }
            return key;
        } catch (OrganizationServiceException e) {
            throw new SshKeyStoreException(String.format("Failed to add private key. '%s'", e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void genKeyPair(String host, String comment, String passPhrase) throws SshKeyStoreException {
        genKeyPair(host, comment, passPhrase, null);
    }

    @Override
    public void genKeyPair(String host, String comment, String passPhrase, String keyMail) throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            if (keyMail == null) {
                keyMail = userId;
            }
            final User myUser = userManager.getUserByAlias(userId);
            final String privateCacheKey = cacheKey(userId, host, PRIVATE);
            final String publicCacheKey = cacheKey(userId, host, PUBLIC);
            cache.remove(privateCacheKey);
            cache.remove(publicCacheKey);
            final String sshPrivateKeyAttributeName = sshKeyAttributeName(host, PRIVATE);
            final String sshPublicKeyAttributeName = sshKeyAttributeName(host, PUBLIC);
            // Be sure keys are not created yet.
            if (myUser.getProfile().getAttribute(sshPrivateKeyAttributeName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            if (myUser.getProfile().getAttribute(sshPublicKeyAttributeName) != null) {
                throw new SshKeyStoreException(String.format("Public key for host: '%s' already exists. ", host));
            }
            // Gen key pair.
            KeyPair keyPair;
            try {
                keyPair = KeyPair.genKeyPair(genJsch, 2, 2048);
            } catch (JSchException e) {
                throw new SshKeyStoreException(e.getMessage(), e);
            }
            keyPair.setPassphrase(passPhrase);

            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            keyPair.writePrivateKey(buff);
            final SshKey privateKey = new SshKey(privateCacheKey, buff.toByteArray());
            buff.reset();
            keyPair.writePublicKey(buff,
                                   comment != null ? comment : (keyMail.indexOf('@') > 0 ? keyMail : (keyMail + "@ide.codenvy.local")));
            final SshKey publicKey = new SshKey(publicCacheKey, buff.toByteArray());
            // Save keys in user attributes
            myUser.getProfile().setAttribute(sshPrivateKeyAttributeName, new String(privateKey.getBytes()));
            myUser.getProfile().setAttribute(sshPublicKeyAttributeName, new String(publicKey.getBytes()));
            userManager.updateUser(myUser);
            // Save in cache
            cache.put(privateCacheKey, privateKey);
            cache.put(publicCacheKey, publicKey);
        } catch (OrganizationServiceException e) {
            throw new SshKeyStoreException(String.format("Failed to generate keys. %s", e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeKeys(String host) throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            final User myUser = userManager.getUserByAlias(userId);
            Iterator<Entry<String, SshKey>> it = cache.iterator();
            while (it.hasNext()) {
                Entry<String, SshKey> el = it.next();
                String[] cacheKey = parseCacheKey(el.getKey());
                if (cacheKey[0].equals(userId) && cacheKey[1].endsWith(host)) {
                    it.remove();
                }
            }
            myUser.getProfile().removeAttribute(sshKeyAttributeName(host, PRIVATE));
            myUser.getProfile().removeAttribute(sshKeyAttributeName(host, PUBLIC));
            userManager.updateUser(myUser);
        } catch (OrganizationServiceException e) {
            throw new SshKeyStoreException(String.format("Failed to remove keys. %s", e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<String> getAll() throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            final User myUser = userManager.getUserByAlias(userId);
            final Map<String, String> allAttributes = myUser.getProfile().getAttributes();

            final Set<String> keys = new HashSet<String>();
            // Check only for private keys.
            for (String str : allAttributes.keySet()) {
                if (str.startsWith(PRIVATE_KEY_ATTRIBUTE_PREFIX)) {
                    keys.add(str.substring(PRIVATE_KEY_ATTRIBUTE_PREFIX.length()));
                }
            }
            return keys;
        } catch (OrganizationServiceException e) {
            throw new SshKeyStoreException(String.format("Unable load user keys. '%s'", e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    private String cacheKey(String user, String host, int i) {
        // Returns something like: andrew00x@gmail.com:codenvy.com:public
        return user + ':' + host + ':' + (i == PRIVATE ? "private" : "public");
    }

    /**
     * Name of attribute of user profile to store SSH key.
     * 
     * @param host host name
     * @param i <code>0</code> if key is private and <code>1</code> if key is public
     * @return user's profile attribute name
     */
    private String sshKeyAttributeName(String host, int i) {
        // Returns something like: ssh.key.private.codenvy.com or ssh.key.public.codenvy.com
        return (i == PRIVATE ? PRIVATE_KEY_ATTRIBUTE_PREFIX : PUBLIC_KEY_ATTRIBUTE_PREFIX) + host;
    }

    private String[] parseCacheKey(String cacheKey) {
        String[] result = new String[3];
        int firstColonIndex = cacheKey.indexOf(":");
        int lastColonIndex = cacheKey.lastIndexOf(":");
        result[0] = cacheKey.substring(0, firstColonIndex); // user
        result[1] = cacheKey.substring(firstColonIndex, lastColonIndex);// host
        result[2] = cacheKey.substring(lastColonIndex, cacheKey.length());// key type
        return result;
    }
}
