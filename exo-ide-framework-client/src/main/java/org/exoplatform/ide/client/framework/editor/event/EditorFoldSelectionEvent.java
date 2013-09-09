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
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: EditorFoldSelectionEvent.java Feb 28, 2013 5:08:29 PM azatsarynnyy $
 */
public class EditorFoldSelectionEvent extends GwtEvent<EditorFoldSelectionHandler> {
    public EditorFoldSelectionEvent() {
        super();
    }

    public static final GwtEvent.Type<EditorFoldSelectionHandler> TYPE = new GwtEvent.Type<EditorFoldSelectionHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorFoldSelectionHandler handler) {
        handler.onEditorFoldSelection(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorFoldSelectionHandler> getAssociatedType() {
        return TYPE;
    }
}
