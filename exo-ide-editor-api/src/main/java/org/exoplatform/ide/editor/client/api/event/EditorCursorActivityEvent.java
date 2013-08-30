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
 * Fires just after some key or mouse event have been happened in editor.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorCursorActivityEvent extends GwtEvent<EditorCursorActivityHandler> {

    public static final GwtEvent.Type<EditorCursorActivityHandler> TYPE =
            new GwtEvent.Type<EditorCursorActivityHandler>();

    /** {@link Editor} instance. */
    private Editor editor;

    /** Cursor row. */
    private int row;

    /** Cursor column. */
    private int column;

    /**
     * Creates new instance of {@link EditorCursorActivityEvent}.
     *
     * @param editor
     * @param row
     * @param column
     */
    public EditorCursorActivityEvent(Editor editor, int row, int column) {
        this.editor = editor;
        this.row = row;
        this.column = column;
    }

    /**
     * Returns {@link Editor} instance.
     *
     * @return
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Returns cursor column.
     *
     * @return
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns cursor row.
     *
     * @return
     */
    public int getRow() {
        return row;
    }

    @Override
    protected void dispatch(EditorCursorActivityHandler handler) {
        handler.onEditorCursorActivity(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorCursorActivityHandler> getAssociatedType() {
        return TYPE;
    }

}
