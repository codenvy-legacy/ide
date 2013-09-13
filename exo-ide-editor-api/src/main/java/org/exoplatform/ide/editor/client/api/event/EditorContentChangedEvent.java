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

package org.exoplatform.ide.editor.client.api.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.editor.client.api.Editor;

/**
 * Fires just after opened in editor content had been changed.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorContentChangedEvent extends GwtEvent<EditorContentChangedHandler> {

    public static final GwtEvent.Type<EditorContentChangedHandler> TYPE =
            new GwtEvent.Type<EditorContentChangedHandler>();

    /** {@link org.exoplatform.ide.editor.client.api.Editor} instance. */
    private Editor editor;

    /**
     * Creates new instance of {@link EditorContentChangedEvent}.
     *
     * @param editor
     */
    public EditorContentChangedEvent(Editor editor) {
        this.editor = editor;
    }

    /**
     * Returns {@link Editor} instance.
     *
     * @return
     */
    public Editor getEditor() {
        return editor;
    }

    @Override
    protected void dispatch(EditorContentChangedHandler handler) {
        handler.onEditorContentChanged(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorContentChangedHandler> getAssociatedType() {
        return TYPE;
    }

}
