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

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Parser Feb 9, 2011 4:55:57 PM evgen $
 */
public abstract class Parser {

    public abstract List<? extends Token> getTokenList(String editorId, JavaScriptObject editor);

    public abstract void getTokenListInBackground(String editorId, JavaScriptObject editor, EditorTokenListPreparedHandler handler);

    public abstract void stopParsing();

}
