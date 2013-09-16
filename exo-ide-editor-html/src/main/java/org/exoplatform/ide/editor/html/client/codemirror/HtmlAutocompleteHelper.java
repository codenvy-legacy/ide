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
package org.exoplatform.ide.editor.html.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.AutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptAutocompleteHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id
 */
public class HtmlAutocompleteHelper extends AutocompleteHelper {

    List<? extends Token> javaScriptCode;

    JavaScriptAutocompleteHelper javaScriptAutocompleteHelper = new JavaScriptAutocompleteHelper();

    public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition,
                                      List<? extends Token> tokenList, String currentLineMimeType) {
        if (MimeType.APPLICATION_JAVASCRIPT.equals(currentLineMimeType)) {
            javaScriptCode =
                    CodeValidator.extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(),
                                              MimeType.APPLICATION_JAVASCRIPT);

            return javaScriptAutocompleteHelper.getTokenBeforeCursor(node, lineNumber, cursorPosition, javaScriptCode,
                                                                     currentLineMimeType);

        }

        return null;
    }

    public boolean isVariable(String nodeType) {
        return false;
    }

    public boolean isPoint(String nodeType, String nodeContent) {
        return false;
    }
}
