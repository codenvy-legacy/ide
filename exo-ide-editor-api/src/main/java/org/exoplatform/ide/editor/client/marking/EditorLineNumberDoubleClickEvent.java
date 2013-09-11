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
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:31:53 AM Mar 26, 2012 evgen $
 */
public class EditorLineNumberDoubleClickEvent extends GwtEvent<EditorLineNumberDoubleClickHandler> {

    public static final GwtEvent.Type<EditorLineNumberDoubleClickHandler> TYPE = new Type<EditorLineNumberDoubleClickHandler>();

    private int lineNumber;

    /** @param lineNumber */
    public EditorLineNumberDoubleClickEvent(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorLineNumberDoubleClickHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorLineNumberDoubleClickHandler handler) {
        handler.onEditorLineNumberDoubleClick(this);
    }

    /** @return the lineNuber */
    public int getLineNumber() {
        return lineNumber;
    }

}
