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
package org.exoplatform.ide.editor.client.marking;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user makes right mouse click on line number.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 8, 2012 4:35:31 PM anya $
 */
public class EditorLineNumberContextMenuEvent extends GwtEvent<EditorLineNumberContextMenuHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<EditorLineNumberContextMenuHandler> TYPE =
            new GwtEvent.Type<EditorLineNumberContextMenuHandler>();

    /** Number of the line, where right mouse click was called. */
    private int lineNumber;

    /** Context menu coordinates. */
    private int x, y;

    /**
     * @param lineNumber
     *         number of the line, where right mouse click was called
     * @param x
     *         left corner position of the context menu
     * @param y
     *         top corner position of the context menu
     */
    public EditorLineNumberContextMenuEvent(int lineNumber, int x, int y) {
        this.lineNumber = lineNumber;
        this.x = x;
        this.y = y;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorLineNumberContextMenuHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorLineNumberContextMenuHandler handler) {
        handler.onEditorLineNumberContextMenu(this);
    }

    /** @return the lineNumber */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the x left corner position of the context menu */
    public int getX() {
        return x;
    }

    /** @return the y top corner position of the context menu */
    public int getY() {
        return y;
    }
}
