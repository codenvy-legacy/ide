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
package org.exoplatform.ide.editor.javascript.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.*;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.codeassitant.*;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;

import java.util.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptCodeAssistant Feb 24, 2011 11:26:32 AM evgen $
 */
public class JavaScriptCodeAssistant extends CodeAssistant implements Comparator<Token> {

    public interface JavaScriptBundle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/javascript/client/tokens/java_script_api.js")
        ExternalTextResource jsApiTokens();
    }

    private static List<Token> defaultTokens;

    private static Map<String, Token> tokensByFQN = new HashMap<String, Token>();

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
    public void autocompleteCalled(final Editor editor, final int cursorOffsetX, final int cursorOffsetY,
                                   final List<Token> tokenList, String lineMimeType, final Token currentToken) {
        this.editor = editor;
        this.posX = cursorOffsetX;
        this.posY = cursorOffsetY;
        try {
            parseTokenLine(editor.getLineText(editor.getCursorRow()), editor.getCursorColumn());

            if (defaultTokens == null) {
                JavaScriptBundle buandle = GWT.create(JavaScriptBundle.class);
                buandle.jsApiTokens().getText(new ResourceCallback<TextResource>() {

                    @Override
                    public void onSuccess(TextResource resource) {
                        JSONObject obj = new JSONObject(parseJson(resource.getText()));
                        JSONTokenParser parser = new JSONTokenParser();

                        defaultTokens = parser.getTokens(obj.get("javascript_tokens").isArray());

                        List<Token> jsApiTokens = parser.getTokens(obj.get("java_script_grobal_var").isArray());
                        defaultTokens.addAll(jsApiTokens);

                        jsApiTokens.addAll(parser.getTokens(obj.get("java_script_api").isArray()));

                        for (Token t : jsApiTokens) {
                            tokensByFQN.put(t.getName().toLowerCase(), t);
                        }

                        autocompletion(editor.getCursorRow(), tokenList, currentToken);
                    }

                    @Override
                    public void onError(ResourceException e) {
                        Log.info(e.getMessage());
                    }
                });

                return;
            }

            autocompletion(editor.getCursorRow(), tokenList, currentToken);
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
    private void autocompletion(int lineNum, List<Token> tokenList, Token currentToken) {

        List<Token> tokens = new ArrayList<Token>();

        if (beforeToken.endsWith(".")) {
            Token clazz = null;
            if (currentToken != null && currentToken.getType() == TokenType.VARIABLE) {
                String type = currentToken.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
                clazz = tokensByFQN.get(type.toLowerCase());
            } else {
                String fqn = beforeToken.substring(0, beforeToken.length());
                String[] posFQN = fqn.split("[^A-Za-z0-9_]+");
                if (posFQN.length > 0) {
                    clazz = tokensByFQN.get(posFQN[posFQN.length - 1].toLowerCase());
                }
            }
            if (clazz != null && clazz.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
                tokens.addAll(clazz.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
            }
        } else {
            tokens.addAll(defaultTokens);
            List<Token> tokenFromParser = getTokenJavaScript(tokenList);
            tokens.addAll(getTokensForLine(lineNum, tokenFromParser));
        }
        Collections.sort(tokens, this);
        openForm(tokens, new JavaScriptTokenWidgetFactory(), this);
    }

    /**
     * @param lineNum
     * @param tokenFromParser
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Token> getTokensForLine(int lineNum, List<Token> tokenFromParser) {
        List<Token> tokens = new ArrayList<Token>();

        Token tok = null;
        for (Token t : tokenFromParser) {
            if (t.getName() == null) {
                continue;
            }
            tokens.add(t);
            NumericProperty s = t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty();
            TokenProperty f = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
            if (f != null && f.isNumericProperty() != null && s.numericValue().intValue() < lineNum
                && f.isNumericProperty().numericValue().intValue() > lineNum) {
                tok = t;
            }

        }
        if (tok != null && tok.getType() == TokenType.FUNCTION) {
            tokens.addAll(getTokensInFunction(lineNum, tok));
        }
        return tokens;
    }

    /**
     * @param lineNum
     * @param tok
     * @return
     */
    private List<Token> getTokensInFunction(int lineNum, Token tok) {
        List<Token> tokens = new ArrayList<Token>();
        if (tok.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
            for (Token t : tok.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
                if (t.getName() == null) {
                    continue;
                }
                NumericProperty s = t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty();
                if (s.numericValue().intValue() < lineNum) {
                    tokens.add(t);
                    if (t.getType() == TokenType.FUNCTION) {
                        tokens.addAll(getTokensInFunction(lineNum, t));
                    }
                }
            }
        }

        return tokens;
    }

    /** @param line */
    private void parseTokenLine(String line, int cursorPos) {
        String tokenLine = "";
        tokenToComplete = "";
        afterToken = "";
        beforeToken = "";
        if (line.length() > cursorPos - 1) {
            afterToken = line.substring(cursorPos - 1, line.length());
            tokenLine = line.substring(0, cursorPos - 1);

        } else {
            afterToken = "";
            if (line.endsWith(" ")) {
                tokenToComplete = "";
                beforeToken = line;
                return;
            }

            tokenLine = line;
        }

        for (int i = tokenLine.length() - 1; i >= 0; i--) {
            switch (tokenLine.charAt(i)) {
                case ' ':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case '.':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case '(':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case ')':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case '{':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case '}':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case ';':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case '[':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                case ']':
                    beforeToken = tokenLine.substring(0, i + 1);
                    tokenToComplete = tokenLine.substring(i + 1);
                    return;

                default:
                    break;
            }
            beforeToken = "";
            tokenToComplete = tokenLine;
        }

    }

    private void printTokens(List<? extends Token> tokens, int i) {
        String spacer = "";
        for (int j = 0; j < i; j++) {
            spacer += " ";
        }
        i++;
        for (Token t : tokens) {
            TokenProperty p = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
            if (p != null && p.isNumericProperty() != null)
                if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
                    && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null) {
                    printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i);
                }
        }
    }

    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(Token t1, Token t2) {
      /*
       * If tokens have the same types, than compare in alphabetic order.
       */
        if (t1.getType() == t2.getType()) {
            return t1.getName().compareTo(t2.getName());
        }

      /*
       * At the begin of list must be variables.
       */
        if (t2.getType() == TokenType.VARIABLE) {
            return 1;
        }
        if (t1.getType() == TokenType.VARIABLE) {
            return -1;
        }

        if (t2.getType() == TokenType.FUNCTION) {
            return 1;
        }
        if (t1.getType() == TokenType.FUNCTION) {
            return -1;
        }

        if (t2.getType() == TokenType.CLASS) {
            return 1;
        }
        if (t1.getType() == TokenType.CLASS) {
            return -1;
        }

        return t1.getName().compareTo(t2.getName());
    }

    /** @param tokenFromParser */
    @SuppressWarnings("unchecked")
    protected List<Token> getTokenJavaScript(List<Token> tokenFromParser) {
        List<Token> tokens = new ArrayList<Token>();

        String tagName = "script";

        for (int i = 0; i < tokenFromParser.size(); i++) {
            Token token = tokenFromParser.get(i);
            if (token.getName() == null)
                continue;

            if (token.getProperty(TokenProperties.MIME_TYPE).isStringProperty().stringValue()
                     .equals(MimeType.APPLICATION_JAVASCRIPT)) {
                // get all subtokens from tag "<script>"
                if (token.getName().equals(tagName) && token.getType().equals(TokenType.TAG)) {
                    if (token.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
                        tokens.addAll(token.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
                    }
                } else {
                    tokens.add(token);
                }
            } else if (token.hasProperty(TokenProperties.SUB_TOKEN_LIST)
                       && token.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null) {
                tokens.addAll(getTokenJavaScript((List<Token>)token.getProperty(TokenProperties.SUB_TOKEN_LIST)
                                                                   .isArrayProperty().arrayValue()));
            }
        }

        return tokens;
    }
}
