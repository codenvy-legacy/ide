package org.exoplatform.ide.extension.ssh.shared;

/**
 * See {@link org.exoplatform.ide.extension.ssh.shared.KeyItem}.
 */
public class KeyItemImpl implements KeyItem {
    private String  host;
    private boolean hasPublicKey;

    public KeyItemImpl(String host, boolean hasPublicKey) {
        this.host = host;
        this.hasPublicKey = hasPublicKey;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean isHasPublicKey() {
        return hasPublicKey;
    }

    @Override
    public void setHasPublicKey(boolean hasPublicKey) {
        this.hasPublicKey = hasPublicKey;
    }
}
