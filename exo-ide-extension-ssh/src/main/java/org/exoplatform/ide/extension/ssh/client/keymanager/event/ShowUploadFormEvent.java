package org.exoplatform.ide.extension.ssh.client.keymanager.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to show user upload key form.
 */
public class ShowUploadFormEvent extends GwtEvent<ShowUploadFormHandler> {
    public static final GwtEvent.Type<ShowUploadFormHandler> TYPE = new Type<ShowUploadFormHandler>();

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowUploadFormHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(ShowUploadFormHandler handler) {
        handler.onShowSshKeyUploadForm(this);
    }
}
