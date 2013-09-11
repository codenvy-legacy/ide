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
package org.exoplatform.ide.editor.xml.client.codeassistant;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.codeassitant.*;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.xml.client.codeassistant.ui.XmlTokenWidget;

import java.util.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: XmlCodeAssistant Mar 1, 2011 5:19:53 PM evgen $
 */
public class XmlCodeAssistant extends CodeAssistant implements TokenWidgetFactory, Comparator<Token> {

    private Map<String, Token> tokens = new HashMap<String, Token>();

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarkClicked(org.exoplatform.ide.editor.client.api.Editor,
     *      java.util.List, int, int, java.lang.String)
     */
    @Override
    public void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
                                 String fileMimeType) {
    }

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.client.api.Editor,
     *      java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String,
     *      org.exoplatform.ide.editor.api.codeassitant.Token)
     */
    @Override
    public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
                                   String lineMimeType, Token currentToken) {
        this.editor = editor;
        this.posX = cursorOffsetX;
        this.posY = cursorOffsetY;
        try {
            parseTokenLine(editor.getLineText(editor.getCursorRow()), editor.getCursorColumn());
            if (tokenToComplete.endsWith(" ")) {
                beforeToken += tokenToComplete;
                tokenToComplete = "";
            }
            tokens.clear();
            List<Token> tok = new ArrayList<Token>();
            filterTokens(tokenList);
            tok.addAll(tokens.values());
            Collections.sort(tok, this);
            openForm(tok, this, this);
        } catch (Exception e) {
            Log.info(e.getMessage());
        }
    }

    /**
     * @param tokenList
     * @return
     */
    @SuppressWarnings("unchecked")
    private void filterTokens(List<Token> tokenList) {
        for (Token t : tokenList) {
            if (t.getName() != null && t.getType() == TokenType.TAG) {
                tokens.put(t.getName(), t);
                t.setProperty(TokenProperties.CODE, new StringProperty("<" + t.getName() + "></" + t.getName() + ">"));
            }
            if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
                && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null) {
                filterTokens((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
            }
        }
    }

    private void parseTokenLine(String line, int cursorPos) {
        String tokenLine = "";
        afterToken = line.substring(cursorPos - 1, line.length());
        tokenLine = line.substring(0, cursorPos - 1);
        if (tokenLine.contains("<")) {
            beforeToken = tokenLine.substring(0, tokenLine.lastIndexOf("<") + 1);
            tokenLine = tokenLine.substring(tokenLine.lastIndexOf("<") + 1, tokenLine.length());
            if (tokenLine.contains(">")) {
                tokenToComplete = "";
                beforeToken = line.substring(0, cursorPos - 1);
            } else {
                tokenToComplete = tokenLine;
            }
        } else {
            beforeToken = tokenLine;
            tokenToComplete = "";
        }

    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api
     * .codeassitant.Token) */
    @Override
    public TokenWidget buildTokenWidget(Token token) {
        return new XmlTokenWidget(token);
    }

    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(Token t1, Token t2) {
        return t1.getName().compareToIgnoreCase(t2.getName());
    }
}
