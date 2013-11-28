package com.codenvy.ide.ext.ssh.server;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.exoplatform.services.security.ConversationState;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DummySshKeyStore implements SshKeyStore {
    private static final int          PRIVATE = 0;
    private static final int          PUBLIC  = 1;

    private final Map<String, SshKey> myKeys  = new HashMap<String, SshKey>();
    private final Lock                lock    = new ReentrantLock();
    private final JSch                genJsch;

    public DummySshKeyStore() {
        this.genJsch = new JSch();
    }

    @Override
    public void addPrivateKey(String host, byte[] key) throws SshKeyStoreException {
        lock.lock();
        try {
            final String keyName = keyName(getUserId(), host, PRIVATE);
            if (myKeys.get(keyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            myKeys.put(keyName, new SshKey(keyName, key));
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
            SshKey key = myKeys.get(keyName(userId, host, i));
            if (key == null) {
                final String keyNamePattern = keyName(userId, "", i); // matched to all private|public keys for user
                for (Map.Entry<String, SshKey> entry : myKeys.entrySet()) {
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
            if (myKeys.get(privateKeyName) != null) {
                throw new SshKeyStoreException(String.format("Private key for host: '%s' already exists. ", host));
            }
            if (myKeys.get(publicKeyName) != null) {
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
            myKeys.put(privateKeyName, privateKey);
            myKeys.put(publicKeyName, publicKey);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeKeys(String host) throws SshKeyStoreException {
        lock.lock();
        try {
            myKeys.remove(keyName(getUserId(), host, PRIVATE));
            myKeys.remove(keyName(getUserId(), host, PUBLIC));
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
            for (String str : myKeys.keySet()) {
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

    private String keyName(String user, String host, int i) {
        return user + ':' + (i == PRIVATE ? "private" : "public") + ':' + host;
    }
}
