package com.codenvy.ide.ext.ssh.server;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.dto.server.JsonStringMapImpl;
import com.codenvy.ide.ext.ssh.dto.SshKeyEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.exoplatform.services.security.ConversationState;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DummySshKeyStore implements SshKeyStore, Startable {

    private static final Logger LOG                             = LoggerFactory.getLogger(DummySshKeyStore.class);
    private static final int    PRIVATE                         = 0;
    private static final int    PUBLIC                          = 1;

    private static final String SSHKEYS_STORE_LOCATION_PROPERTY = "codenvy.sshkeystore.location";

    private static final String DEFAULT_STORE_LOCATION_VALUE    = System.getProperty("java.io.tmpdir");

    private static final String SSHKEYS_FILE_NAME               = "sshkeys.json";

    private Map<String, SshKey> myKeys;
    private final Lock          lock                            = new ReentrantLock();
    private final JSch          genJsch;
    private final String        sshkeysLocationDir;
    private String              fullSshKeysStorageLocation;

    public DummySshKeyStore() {
        String sshKeysLocationValue = System.getProperty(SSHKEYS_STORE_LOCATION_PROPERTY);
        this.sshkeysLocationDir =
                                  (sshKeysLocationValue != null && !sshKeysLocationValue.isEmpty()) ? sshKeysLocationValue
                                      : DEFAULT_STORE_LOCATION_VALUE;
        this.genJsch = new JSch();
    }

    private Map<String, SshKey> loadSshKeysFromStorage() {
        Map<String, SshKey> sshKeys = new HashMap<String, SshKey>();
        File sshKeysStorage = new File(fullSshKeysStorageLocation);
        if (sshKeysStorage.exists()) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(sshKeysStorage);
                Map<String, SshKeyEntry> savedKeys = DtoFactory.getInstance().createMapDtoFromJson(input, SshKeyEntry.class);

                for (String key : savedKeys.keySet()) {
                    SshKeyEntry sshKeyEntry = savedKeys.get(key);
                    sshKeys.put(key, new SshKey(sshKeyEntry.getIdentifier(), sshKeyEntry.getBytes().getBytes()));
                }
            } catch (IOException e) {
                LOG.error("Cannot load SSH keys from local storage.", e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return sshKeys;
    }

    private void saveSshKeysToStorage() throws SshKeyStoreException {
        File sshKeysStorage = new File(fullSshKeysStorageLocation);
        if (!sshKeysStorage.exists()) {
            sshKeysStorage.getParentFile().mkdirs();
            try {
                sshKeysStorage.createNewFile();
            } catch (IOException e) {
                throw new SshKeyStoreException("Cannot save SSH keys to local storage.", e);
            }
        }

        FileOutputStream outputStream = null;
        Map<String, SshKeyEntry> keysToSave = new JsonStringMapImpl<SshKeyEntry>(new HashMap<String, SshKeyEntry>());
        for (String key : getMyKeys().keySet()) {
            SshKey sshKey = getMyKeys().get(key);
            SshKeyEntry sshKeyEntry =
                                      DtoFactory.getInstance().createDto(SshKeyEntry.class).withIdentifier(sshKey.getIdentifier())
                                                .withBytes(new String(sshKey.getBytes()));// Expected keys already encoded with Base64
            keysToSave.put(key, sshKeyEntry);
        }

        try {
            outputStream = new FileOutputStream(sshKeysStorage);
            outputStream.write(DtoFactory.getInstance().toJson(keysToSave).getBytes());
        } catch (FileNotFoundException e) {
            throw new SshKeyStoreException("Cannot save SSH keys to local storage.", e);
        } catch (IOException e) {
            throw new SshKeyStoreException("Cannot save SSH keys to local storage.", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void addPrivateKey(String host, byte[] key) throws SshKeyStoreException {
        lock.lock();
        try {
            final String keyName = keyName(getUserId(), host, PRIVATE);
            if (getMyKeys().get(keyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            getMyKeys().put(keyName, new SshKey(keyName, key));
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
            SshKey key = getMyKeys().get(keyName(userId, host, i));
            if (key == null) {
                final String keyNamePattern = keyName(userId, "", i); // matched to all private|public keys for user
                for (Map.Entry<String, SshKey> entry : getMyKeys().entrySet()) {
                    if (entry.getKey().startsWith(keyNamePattern)) {
                        if (host.endsWith(entry.getKey().substring(keyNamePattern.length()))) {
                            return entry.getValue();
                        }
                    }
                }
            }
            return key;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void genKeyPair(String host, String comment, String passPhrase) throws SshKeyStoreException {
        genKeyPair(host, comment, passPhrase, null);
    }

    public void genKeyPair(String host, String comment, String passPhrase, String keyMail) throws SshKeyStoreException {
        lock.lock();
        try {
            final String userId = getUserId();
            if (keyMail == null) {
                keyMail = userId;
            }
            final String privateKeyName = keyName(userId, host, PRIVATE);
            final String publicKeyName = keyName(userId, host, PUBLIC);
            // Be sure keys are not created yet.
            if (getMyKeys().get(privateKeyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            if (getMyKeys().get(publicKeyName) != null) {
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
            final SshKey privateKey = new SshKey(privateKeyName, buff.toByteArray());
            buff.reset();
            keyPair.writePublicKey(buff,
                                   comment != null ? comment : (keyMail.indexOf('@') > 0 ? keyMail : (keyMail + "@ide.codenvy.local")));
            final SshKey publicKey = new SshKey(publicKeyName, buff.toByteArray());
            getMyKeys().put(privateKeyName, privateKey);
            getMyKeys().put(publicKeyName, publicKey);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeKeys(String host) throws SshKeyStoreException {
        lock.lock();
        try {
            getMyKeys().remove(keyName(getUserId(), host, PRIVATE));
            getMyKeys().remove(keyName(getUserId(), host, PUBLIC));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<String> getAll() throws SshKeyStoreException {
        lock.lock();
        try {
            final String keyNamePattern = keyName(getUserId(), "", PRIVATE); // matched to all private keys for user
            final Set<String> keys = new HashSet<String>();
            for (String str : getMyKeys().keySet()) {
                if (str.startsWith(keyNamePattern)) {
                    keys.add(str.substring(keyNamePattern.length()));
                }
            }
            return keys;
        } finally {
            lock.unlock();
        }
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    private Map<String, SshKey> getMyKeys() {
        if (myKeys == null) {
            fullSshKeysStorageLocation =
                                         new StringBuilder(sshkeysLocationDir).append("/").append(getUserId()).append("/").append(SSHKEYS_FILE_NAME)
                                                                              .toString();
            myKeys = loadSshKeysFromStorage();
        }
        return myKeys;
    }

    private String keyName(String user, String host, int i) {
        return user + ':' + (i == PRIVATE ? "private" : "public") + ':' + host;
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        // Do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        try {
            saveSshKeysToStorage();
        } catch (SshKeyStoreException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
