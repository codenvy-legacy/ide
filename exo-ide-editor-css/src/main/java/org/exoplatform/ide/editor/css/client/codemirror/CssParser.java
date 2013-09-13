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
package org.exoplatform.ide.editor.css.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class CssParser extends CodeMirrorParserImpl {

    /**
     * @see org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl#getTokenList(java.lang.String,
     *      com.google.gwt.core.client.JavaScriptObject)
     */
    @Override
    public List<TokenBeenImpl> getTokenList(String editorId, JavaScriptObject editor) {
        return new ArrayList<TokenBeenImpl>();
    }

}