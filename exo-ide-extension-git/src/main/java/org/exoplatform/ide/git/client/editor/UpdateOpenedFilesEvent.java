package org.exoplatform.ide.git.client.editor;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to update content in current opened files.
 */
public class UpdateOpenedFilesEvent extends GwtEvent<UpdateOpenedFilesHandler> {
    public static final Type TYPE = new Type<UpdateOpenedFilesHandler>();

    /** {@inheritDoc} */
    @Override
    public Type<UpdateOpenedFilesHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(UpdateOpenedFilesHandler handler) {
        handler.onUpdateOpenedFiles(this);
    }
}
