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
package org.exoplatform.ide.client.framework.editor.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class EditorGoToLineEvent extends GwtEvent<EditorGoToLineHandler> {

    public static final GwtEvent.Type<EditorGoToLineHandler> TYPE = new GwtEvent.Type<EditorGoToLineHandler>();

    private int lineNumber;

    private int columnNumber = 1;

    public EditorGoToLineEvent(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public EditorGoToLineEvent(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorGoToLineHandler handler) {
        handler.onEditorGoToLine(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorGoToLineHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the numberLine */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the columnNumber */
    public int getColumnNumber() {
        return columnNumber;
    }

}
