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
 * Event occurs, when user calls context menu in editor.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 26, 2012 5:57:23 PM anya $
 */
public class EditorContextMenuEvent extends GwtEvent<EditorContextMenuHandler> {

    /** Type, used to register the event. */
    public static final GwtEvent.Type<EditorContextMenuHandler> TYPE = new GwtEvent.Type<EditorContextMenuHandler>();

    /** Coordinates of the context menu. */
    private int x, y;

    /** {@link org.exoplatform.ide.editor.client.api.Editor} instance. */
    private Editor editor;

    public EditorContextMenuEvent(Editor editor, int x, int y) {
        this.editor = editor;
        this.x = x;
        this.y = y;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorContextMenuHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorContextMenuHandler handler) {
        handler.onEditorContextMenu(this);
    }

    /** @return the x */
    public int getX() {
        return x;
    }

    /** @return the y */
    public int getY() {
        return y;
    }

    /**
     * Returns {@link Editor} instance.
     *
     * @return
     */
    public Editor getEditor() {
        return editor;
    }
}
