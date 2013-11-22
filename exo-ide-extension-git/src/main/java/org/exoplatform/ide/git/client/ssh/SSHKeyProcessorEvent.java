package org.exoplatform.ide.git.client.ssh;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event indicate that we call SSH key process before making request to Git server.
 */
public class SSHKeyProcessorEvent extends GwtEvent<SSHKeyProcessorHandler> {
    private final String                   vcsUrl;
    private final SSHKeyProcessor.Callback callback;
    private final boolean                  updatePublicKey;

    public SSHKeyProcessorEvent(String vcsUrl, boolean updatePublicKey, SSHKeyProcessor.Callback callback) {
        this.vcsUrl = vcsUrl;
        this.callback = callback;
        this.updatePublicKey = updatePublicKey;
    }

    public static final Type<SSHKeyProcessorHandler> TYPE = new Type<SSHKeyProcessorHandler>();

    /** {@inheritDoc} */
    @Override
    public Type<SSHKeyProcessorHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(SSHKeyProcessorHandler handler) {
        handler.onSSHKeyProcess(this);
    }

    /**
     * Get Git url for repository.
     *
     * @return repository url
     */
    public String getVcsUrl() {
        return vcsUrl;
    }

    /**
     * user callback on success operation of SSH key process.
     *
     * @return callback
     */
    public SSHKeyProcessor.Callback getCallback() {
        return callback;
    }

    /**
     * Should we update private key for specific host or not.
     *
     * @return true if should
     */
    public boolean isUpdatePublicKey() {
        return updatePublicKey;
    }
}
