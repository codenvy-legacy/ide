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
package org.exoplatform.ide.editor.java.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.AutocompleteHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class JavaAutocompleteHelper extends AutocompleteHelper {

    JavaCodeValidator javaCodeValidator = new JavaCodeValidator();

    public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition,
                                      List<? extends Token> tokenList, String currentLineMimeType) {
        return getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList);
    }

    /**
     * @param node
     * @param lineNumber
     * @param cursorPosition
     * @param tokenList
     * @return
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
                    return new TokenBeenImpl(tokenBeforeCursor.getName(), tokenBeforeCursor.getType(), lineNumber,
                                             tokenBeforeCursor.getMimeType(), tokenBeforeCursor.getElementType(),
                                             tokenBeforeCursor.getModifiers(), tokenBeforeCursor.getFqn());
                }
            }

            // search fqn among default packages
            String fqn = javaCodeValidator.getFqnFromDefaultPackages(nodeContent);
            if (fqn != null)
                return new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_JAVA, nodeContent,
                                         Arrays.asList(Modifier.STATIC), fqn);

            // search fqn among the import statements from the import block
            List<TokenBeenImpl> importStatementBlock =
                    JavaCodeValidator.getImportStatementBlock((List<TokenBeenImpl>)tokenList);
            for (TokenBeenImpl importStatement : importStatementBlock) {
                if (importStatement.getElementType().endsWith(nodeContent)) {
                    return (Token)new TokenBeenImpl(null, TokenType.TYPE, lineNumber, MimeType.APPLICATION_JAVA,
                                                    nodeContent, Arrays.asList(Modifier.STATIC), importStatement.getElementType());
                }
            }

        }

        // if this is "name_" or " _" cases, return Token of parent element, like method or class
        else {
            return (Token)getContainerToken(lineNumber, (List<TokenBeenImpl>)tokenList);
        }

        return null;
    }

    protected static TokenBeenImpl getGenericToken(String nodeContent, int targetLineNumber,
                                                   List<TokenBeenImpl> tokenList) {
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
            // test if nearest token is within the method
            if (nearestToken.getParentToken() != null && TokenType.METHOD.equals(nearestToken.getParentToken().getType())) {
                // search as local variables among the subTokens
                genericToken =
                        searchGenericTokenAmongMethodVariables(nodeContent, nearestToken, nearestToken.getParentToken());
                if (genericToken != null)
                    return genericToken;

                // search among the parameters of method
                genericToken =
                        searchGenericTokenAmongParameters(nodeContent, nearestToken.getParentToken().getParameters());
                if (genericToken != null)
                    return genericToken;

                // search among the properties (fields) of class
                genericToken =
                        searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken().getParentToken());
                if (genericToken != null)
                    return genericToken;
            }

            // test if nearest token is method token
            else if (TokenType.METHOD.equals(nearestToken.getType())) {
                // search among the parameters of method
                genericToken = searchGenericTokenAmongParameters(nodeContent, nearestToken.getParameters());
                if (genericToken != null)
                    return genericToken;

                // search among the properties (fields) of class
                genericToken = searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken());
                if (genericToken != null)
                    return genericToken;
            }

            // trying to search generic token whitin the scriptlets of JSP, or Groovy Template files
            else {
                // search among the properties (fields) of class
                genericToken = searchGenericTokenAmongProperties(nodeContent, nearestToken.getParentToken());
                if (genericToken != null)
                    return genericToken;
            }
        }

        return null;
    }

    public boolean isVariable(String nodeType) {
        return JavaParser.isJavaVariable(nodeType);
    }

    public boolean isPoint(String nodeType, String nodeContent) {
        return JavaParser.isPoint(nodeType, nodeContent);
    }
}
