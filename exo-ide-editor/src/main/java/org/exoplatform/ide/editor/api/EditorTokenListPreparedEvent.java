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

package org.exoplatform.ide.editor.api;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import java.util.List;

/**
 * Fires just after opened in editor content had been changed. Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version @version $Id: $
 */

public class EditorTokenListPreparedEvent extends GwtEvent<EditorTokenListPreparedHandler> {

    public static final GwtEvent.Type<EditorTokenListPreparedHandler> TYPE =
            new GwtEvent.Type<EditorTokenListPreparedHandler>();

    private String editorId;

    private List<? extends Token> tokenList;

    public EditorTokenListPreparedEvent(String editorId, List<? extends Token> tokenList) {
        this.editorId = editorId;
        this.tokenList = tokenList;
    }

    public String getEditorId() {
        return editorId;
    }

    public List<? extends Token> getTokenList() {
        return tokenList;
    }

    @Override
    protected void dispatch(EditorTokenListPreparedHandler handler) {
        handler.onEditorTokenListPrepared(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorTokenListPreparedHandler> getAssociatedType() {
        return TYPE;
    }

}
