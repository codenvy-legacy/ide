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
package com.codenvy.ide.api.event;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires by editor when change dirty state(content modified or saved)
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class EditorDirtyStateChangedEvent extends GwtEvent<EditorDirtyStateChangedHandler> {

    public static final GwtEvent.Type<EditorDirtyStateChangedHandler> TYPE = new Type<EditorDirtyStateChangedHandler>();

    private EditorPartPresenter editor;

    /** @param editor */
    public EditorDirtyStateChangedEvent(EditorPartPresenter editor) {
        super();
        this.editor = editor;
    }

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorDirtyStateChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(EditorDirtyStateChangedHandler handler) {
        handler.onEditorDirtyStateChanged(this);
    }

    /** @return the editor */
    public EditorPartPresenter getEditor() {
        return editor;
    }
}
