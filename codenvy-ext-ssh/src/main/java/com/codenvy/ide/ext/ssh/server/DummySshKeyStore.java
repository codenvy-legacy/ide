package com.codenvy.ide.ext.ssh.server;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.dto.server.JsonStringMapImpl;
import com.codenvy.ide.ext.ssh.dto.SshKeyEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class DummySshKeyStore implements SshKeyStore {
    private static final Logger LOG = LoggerFactory.getLogger(DummySshKeyStore.class);

    private static final int PRIVATE = 0;
    private static final int PUBLIC  = 1;

    private final Lock                lock;
    private final JSch                genJsch;
    private final String              storageFile;
    private final Map<String, SshKey> store;

    @Inject
    public DummySshKeyStore(@Nullable @Named("ssh.key_store_location") String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            storageFile = System.getProperty("java.io.tmpdir") + "/ssh_keys.json";
        } else {
            storageFile = dirPath + "/ssh_keys.json";
        }
        this.store = new HashMap<>();
        this.genJsch = new JSch();
        this.lock = new ReentrantLock();
    }

    @Override
    public void addPrivateKey(String host, byte[] key) throws SshKeyStoreException {
        lock.lock();
        try {
            final String keyName = keyName(getUserId(), host, PRIVATE);
            if (store.get(keyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            store.put(keyName, new SshKey(keyName, key));
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
            SshKey key = store.get(keyName(userId, host, i));
            if (key == null) {
                final String keyNamePattern = keyName(userId, "", i); // matched to all private|public keys for user
                for (Map.Entry<String, SshKey> entry : store.entrySet()) {
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
            if (store.get(privateKeyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            if (store.get(publicKeyName) != null) {
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
            store.put(privateKeyName, privateKey);
            store.put(publicKeyName, publicKey);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeKeys(String host) throws SshKeyStoreException {
        lock.lock();
        try {
            store.remove(keyName(getUserId(), host, PRIVATE));
            store.remove(keyName(getUserId(), host, PUBLIC));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<String> getAll() throws SshKeyStoreException {
        lock.lock();
        try {
            final String keyNamePattern = keyName(getUserId(), "", PRIVATE); // matched to all private keys for user
            final Set<String> keys = new HashSet<>();
            for (String str : store.keySet()) {
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
        return EnvironmentContext.getCurrent().getUser().getName();
    }

    private String keyName(String user, String host, int i) {
        return user + ':' + (i == PRIVATE ? "private" : "public") + ':' + host;
    }

    @PostConstruct
    public void init() {
        lock.lock();
        try {
            Map<String, SshKey> map = new HashMap<>();
            File sshKeysStorage = new File(storageFile);
            if (sshKeysStorage.exists()) {
                try (FileInputStream input = new FileInputStream(sshKeysStorage)) {
                    Map<String, SshKeyEntry> savedKeys = DtoFactory.getInstance().createMapDtoFromJson(input, SshKeyEntry.class);
                    for (String key : savedKeys.keySet()) {
                        SshKeyEntry sshKeyEntry = savedKeys.get(key);
                        map.put(key, new SshKey(sshKeyEntry.getIdentifier(), sshKeyEntry.getBytes().getBytes()));
                    }
                }
            }
            store.putAll(map);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @PreDestroy
    public void stop() {
        lock.lock();
        try {
            Map<String, SshKeyEntry> keysToSave = new JsonStringMapImpl<>(new HashMap<String, SshKeyEntry>());
            for (String key : store.keySet()) {
                SshKey sshKey = store.get(key);
                SshKeyEntry sshKeyEntry = DtoFactory.getInstance().createDto(SshKeyEntry.class)
                                                    .withIdentifier(sshKey.getIdentifier())
                                                    .withBytes(new String(sshKey.getBytes()));// Expected keys already encoded with Base64
                keysToSave.put(key, sshKeyEntry);
            }
            File sshKeysStorage = new File(storageFile);
            if (!sshKeysStorage.exists()) {
                // be sure parent directories exist
                sshKeysStorage.getParentFile().mkdirs();
            }
            try (FileOutputStream outputStream = new FileOutputStream(sshKeysStorage)) {
                outputStream.write(DtoFactory.getInstance().toJson(keysToSave).getBytes());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
