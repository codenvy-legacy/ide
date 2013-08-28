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
 * Event occurs, when user tries to cut selected text from editor.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 4, 2012 9:28:46 AM anya $
 */
public class EditorCutTextEvent extends GwtEvent<EditorCutTextHandler> {

    /** Type used to register the event. */
    public static final GwtEvent.Type<EditorCutTextHandler> TYPE = new GwtEvent.Type<EditorCutTextHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorCutTextHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorCutTextHandler handler) {
        handler.onEditorCutText(this);
    }

}
