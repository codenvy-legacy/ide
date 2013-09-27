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
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class EditorFormatTextEvent extends GwtEvent<EditorFormatTextHandler> {

    public static final GwtEvent.Type<EditorFormatTextHandler> TYPE = new GwtEvent.Type<EditorFormatTextHandler>();

    @Override
    protected void dispatch(EditorFormatTextHandler handler) {
        handler.onFormatFile(this);
    }

    @Override
    public Type<EditorFormatTextHandler> getAssociatedType() {
        return TYPE;
    }

}