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
package org.exoplatform.ide.editor.api.codeassitant;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.codeassitant.ui.*;
import org.exoplatform.ide.editor.client.api.Editor;

import java.util.List;

/**
 * Callback abstract class for codeassistant feature.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistant Feb 22, 2011 12:43:13 PM evgen $
 */
public abstract class CodeAssistant implements TokenSelectedHandler, ImportDeclarationSelectedHandler {

    protected String beforeToken;

    protected String tokenToComplete;

    protected String afterToken;

    protected Editor editor;

    protected int posX;

    protected int posY;

    /** @param eventBys */
    public CodeAssistant() {
        super();
    }

    /**
     * If editor support code validation, it's method called when user click on error mark
     *
     * @param editor
     *         instance of current editor
     * @param codeErrorList
     *         list with errors
     * @param markOffsetX
     *         coordinate X
     * @param markOffsetY
     *         coordinate X
     * @param fileMimeType
     *         mime type of current edited file
     */
    public abstract void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
                                          String fileMimeType);

    /**
     * If editor support autocompletion, he calls this method.
     *
     * @param editor
     *         instance of editor
     * @param cursorOffsetX
     *         offset x position of cursor
     * @param cursorOffsetY
     *         offset y position of cursor
     * @param tokenList
     *         List of parsed tokens
     * @param lineMimeType
     *         line mime type
     * @param currentToken
     *         if cursor pleased before dot(i.e. autocompletion called for method or property), contains token with
     *         information of type of variable
     */
    public abstract void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
                                            String lineMimeType, Token currentToken);

    /**
     * Create and show {@link AutocompletionForm}
     *
     * @param tokens
     *         list of tokens
     * @param factory
     *         instance of {@link TokenWidgetFactory}
     * @param handler
     *         callback for autocompletion form
     */
    protected void openForm(List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler) {
        posX = posX - tokenToComplete.length() * 8 + 8;
        new AutocompletionForm(posX, posY, tokenToComplete, tokens, factory, handler);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onStringValueEntered(java.lang.String) */
    @Override
    public void onStringValueEntered(String value) {
        editor.replaceTextAtCurrentLine(beforeToken + value + afterToken, beforeToken.length() + value.length());
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onTokenSelected(org.exoplatform.ide.editor.api
     * .codeassitant.ui.TokenWidget) */
    @Override
    public void onTokenSelected(TokenWidget value) {
        String tokenValue = value.getTokenValue();
        String tokenToPaste = "";
        int newCursorPos = 1;
        switch (value.getToken().getType()) {
            case ATTRIBUTE:
                if (!beforeToken.endsWith(" "))
                    beforeToken += " ";
                tokenToPaste = beforeToken + tokenValue + afterToken;
                newCursorPos = (beforeToken + tokenValue).lastIndexOf("\"");
                break;

            case TAG:
                if (beforeToken.endsWith("<") || beforeToken.endsWith(" "))
                    beforeToken = beforeToken.substring(0, beforeToken.length() - 1);
                tokenToPaste = beforeToken + tokenValue + afterToken;
                if (tokenValue.contains("/"))
                    newCursorPos = (beforeToken + tokenValue).indexOf("/", beforeToken.length()) - 1;
                else
                    newCursorPos = (beforeToken + tokenValue).length() + 1;
                break;

            case FUNCTION:
            case METHOD:
                newCursorPos = (beforeToken + tokenValue).length() + 1;
                if (tokenValue.contains("(")) {
                    newCursorPos = (beforeToken + tokenValue).lastIndexOf('(') + 1;
                }
                tokenToPaste = beforeToken + tokenValue + afterToken;
                break;

            default:
                tokenToPaste = beforeToken + tokenValue + afterToken;
                newCursorPos = beforeToken.length() + tokenValue.length();
                break;
        }
        editor.replaceTextAtCurrentLine(tokenToPaste, newCursorPos);
        if (value.getToken().hasProperty(TokenProperties.FQN)) {
            if (editor instanceof CanInsertImportStatement) {
                String statement = value.getToken().getProperty(TokenProperties.FQN).isStringProperty().stringValue();
                ((CanInsertImportStatement)editor).insertImportStatement(statement);
            }
        }
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onAutoCompleteCanceled() */
    @Override
    public void onAutoCompleteCanceled() {
        editor.setFocus();
    }

    /**
     * Create and show {@link AssistImportDeclarationForm}
     *
     * @param left
     *         position
     * @param top
     *         position
     * @param tokens
     *         list of tokens
     * @param factory
     *         instance of {@link TokenWidgetFactory}
     * @param handler
     *         callback for {@link AssistImportDeclarationForm}
     */
    protected void openImportForm(int left, int top, List<Token> tokens, TokenWidgetFactory factory,
                                  ImportDeclarationSelectedHandler handler) {
        new AssistImportDeclarationForm(left, top, tokens, factory, handler);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.ImportDeclarationSelectedHandler#onImportDeclarationSelected(org.exoplatform
     * .ide.editor.api.codeassitant.Token) */
    @Override
    public void onImportDeclarationSelected(Token token) {
        if (editor instanceof CanInsertImportStatement) {
            String statement = token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
            ((CanInsertImportStatement)editor).insertImportStatement(statement);
        }
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.ImportDeclarationSelectedHandler#onImportCanceled() */
    @Override
    public void onImportCanceled() {
        editor.setFocus();
    }

    /**
     * Takes in a trusted JSON String and evals it.
     *
     * @param JSON
     *         String that you trust
     * @return JavaScriptObject that you can cast to an Overlay Type
     */
    protected native JavaScriptObject parseJson(String json) /*-{
        return eval('(' + json + ')');
    }-*/;

}
