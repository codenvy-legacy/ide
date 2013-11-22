package org.exoplatform.ide.extension.ssh.shared;

/**
 * POJO model for Public key content.
 */
public interface PublicKey {
    String getHost();

    String getKey();

    void setHost(String host);

    void setKey(String key);
}
