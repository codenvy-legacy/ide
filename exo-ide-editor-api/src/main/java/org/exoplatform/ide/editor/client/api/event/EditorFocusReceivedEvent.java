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
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version @version $Id: $
 */

public class EditorFocusReceivedEvent extends GwtEvent<EditorFocusReceivedHandler> {

    public static final GwtEvent.Type<EditorFocusReceivedHandler> TYPE = new GwtEvent.Type<EditorFocusReceivedHandler>();

    /** {@link org.exoplatform.ide.editor.client.api.Editor} instance. */
    private Editor editor;

    /**
     * Creates new instance of {@link EditorFocusReceivedEvent}.
     *
     * @param editorId
     */
    public EditorFocusReceivedEvent(Editor editor) {
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
    protected void dispatch(EditorFocusReceivedHandler handler) {
        handler.onEditorFocusReceived(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorFocusReceivedHandler> getAssociatedType() {
        return TYPE;
    }
}
