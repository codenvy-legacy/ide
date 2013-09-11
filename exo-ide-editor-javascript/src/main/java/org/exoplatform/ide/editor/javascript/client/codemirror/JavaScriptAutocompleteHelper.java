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
package org.exoplatform.ide.editor.javascript.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.AutocompleteHelper;

import java.util.List;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class JavaScriptAutocompleteHelper extends AutocompleteHelper {

    public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition,
                                      List<? extends Token> tokenList, String currentLineMimeType) {
        return getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList);
    }

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core
     * .client.JavaScriptObject,
     *      int, int, java.util.List)
     */
    public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition,
                                      List<? extends Token> tokenList) {
        // interrupt at the end of the line or content
        if ((node == null) || (node).equals("BR")) {
            return null;
        }

        String nodeContent = getStatementBeforePoint(node, cursorPosition);

        TokenBeenImpl tokenBeforeCursor;

        if (nodeContent != null && !nodeContent.isEmpty()) {
            int numberOfChainsBetweenPoint = nodeContent.split("[.]").length; // nodeContent.split("[.]") returns 1 for "name", and 3
            // for "java.lang.Integer"

            // search token for variables like "name._" or "name.ch_"
            if (numberOfChainsBetweenPoint == 1) {
                tokenBeforeCursor = getGenericToken(nodeContent, lineNumber, (List<TokenBeenImpl>)tokenList);
                if (tokenBeforeCursor != null) {
                    TokenBeenImpl newToken =
                            new TokenBeenImpl(tokenBeforeCursor.getName(), tokenBeforeCursor.getType(), lineNumber,
                                              tokenBeforeCursor.getMimeType(), tokenBeforeCursor.getElementType(),
                                              tokenBeforeCursor.getInitializationStatement());

                    return (Token)newToken;
                }
            }
        }

        return null;
    }

    private static TokenBeenImpl getGenericToken(String nodeContent, int targetLineNumber, List<TokenBeenImpl> tokenList) {
        if (tokenList == null || tokenList.size() == 0)
            return null;

        nearestToken = tokenList.get(0);

        for (TokenBeenImpl token : tokenList) {
            // test is Container Token After The CurrentLine
            if (token.getLineNumber() > targetLineNumber)
                break;

            searchNearestToken(targetLineNumber, token);
        }

        TokenBeenImpl genericToken;

        if (nearestToken != null) {
            if (nearestToken.getParentToken() != null) {
                // search as local variables among the subTokens
                genericToken =
                        searchGenericTokenAmongMethodVariables(nodeContent, nearestToken, nearestToken.getParentToken());
                if (genericToken != null) {
                    return genericToken;
                } else {
                    if (nearestToken.getParentToken().getParentToken() != null) {
                        return getGenericToken(nodeContent, nearestToken.getParentToken().getLineNumber() - 1, nearestToken
                                .getParentToken().getParentToken().getSubTokenList());
                    }
                }
            }

            if (TokenType.VARIABLE.equals(nearestToken.getType()) && nodeContent.equals(nearestToken.getName())) {
                return nearestToken;
            }

        }

        return null;
    }

    public boolean isVariable(String nodeType) {
        return JavaScriptParser.isJsVariable(nodeType) || JavaScriptParser.isJsLocalVariable(nodeType);
    }

    public boolean isPoint(String nodeType, String nodeContent) {
        return JavaScriptParser.isPoint(nodeType, nodeContent);
    }
}
