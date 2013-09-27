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
package org.exoplatform.ide.editor.css.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.*;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CssCodeAssistant Feb 22, 2011 2:15:17 PM evgen $
 */
public class CssCodeAssistant extends CodeAssistant {

    public interface CssBundle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/css/client/tokens/css_tokens.js")
        ExternalTextResource cssTokens();
    }

    private static List<Token> cssProperty;

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarkClicked(org.exoplatform.ide.editor.client.api.Editor,
     *      java.util.List, int, int, java.lang.String)
     */
    public void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
                                 String fileMimeType) {
    }

    /**
     * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.client.api.Editor,
     *      java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String,
     *      org.exoplatform.ide.editor.api.codeassitant.Token)
     */
    public void autocompleteCalled(final Editor editor, final int cursorOffsetX, final int cursorOffsetY,
                                   List<Token> tokenList, String lineMimeType, Token currentToken) {
        try {
            this.editor = editor;
            this.posX = cursorOffsetX;
            this.posY = cursorOffsetY;
            if (cssProperty == null) {
                CssBundle buandle = GWT.create(CssBundle.class);
                buandle.cssTokens().getText(new ResourceCallback<TextResource>() {

                    @Override
                    public void onSuccess(TextResource resource) {
                        JSONTokenParser parser = new JSONTokenParser();
                        JSONArray tokenArray = new JSONArray(parseJson(resource.getText()));
                        cssProperty = parser.getTokens(tokenArray);
                        fillTokens(editor.getLineText(editor.getCursorRow()), editor.getCursorColumn());
                    }

                    @Override
                    public void onError(ResourceException e) {
                        Log.info(e.getMessage());
                    }
                });
                return;
            }
            fillTokens(editor.getLineText(editor.getCursorRow()), editor.getCursorColumn());
        } catch (Exception e) {
            Log.info(e.getMessage());
        }
    }

    /**
     * @param cursorOffsetX
     * @param cursorOffsetY
     * @param lineContent
     * @param cursorPositionX
     */
    private void fillTokens(String lineContent, int cursorPositionX) {
        String subToken = lineContent.substring(0, cursorPositionX - 1);
        afterToken = lineContent.substring(cursorPositionX - 1);

        String token = "";
        if (!subToken.endsWith(" ") && !subToken.endsWith(":") && !subToken.endsWith(";") && !subToken.endsWith("}")
            && !subToken.endsWith("{")) {
            String[] split = subToken.split("[/()|&\",' ]+");

            if (split.length != 0) {
                token = split[split.length - 1];
            }
        }
        beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
        tokenToComplete = token;

        openForm(cssProperty, new CssTokenWidgetFactory(), this);
    }

}
