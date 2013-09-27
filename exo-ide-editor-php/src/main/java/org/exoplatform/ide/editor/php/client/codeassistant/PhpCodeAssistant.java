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
package org.exoplatform.ide.editor.php.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.*;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.codeassitant.*;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;
import org.exoplatform.ide.editor.css.client.codeassistant.CssCodeAssistant;
import org.exoplatform.ide.editor.html.client.codeassistant.HtmlCodeAssistant;
import org.exoplatform.ide.editor.javascript.client.codeassistant.JavaScriptCodeAssistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class PhpCodeAssistant extends CodeAssistant implements Comparator<Token> {

    public interface PhpBundle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/php/client/tokens/php_tokens.js")
        ExternalTextResource phpKeyWords();
    }

    private static List<Token> keyWords;

    private int currentLine;

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
    public void autocompleteCalled(final Editor editor, int cursorOffsetX, int cursorOffsetY,
                                   final List<Token> tokenList, String lineMimeType, final Token currentToken) {
        if (MimeType.TEXT_CSS.equals(lineMimeType)) {
            new CssCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType,
                                                      currentToken);
            return;
        }

        if (MimeType.APPLICATION_JAVASCRIPT.equals(lineMimeType)) {
            new JavaScriptCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList,
                                                             lineMimeType, currentToken);
            return;
        }

        if (MimeType.TEXT_HTML.equals(lineMimeType)) {
            new HtmlCodeAssistant().autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType,
                                                       currentToken);
            return;
        }

        this.editor = editor;
        this.posX = cursorOffsetX;
        this.posY = cursorOffsetY;
        currentLine = editor.getCursorRow();
        if (keyWords == null) {
            PhpBundle bundle = GWT.create(PhpBundle.class);
            try {
                bundle.phpKeyWords().getText(new ResourceCallback<TextResource>() {

                    @Override
                    public void onSuccess(TextResource resource) {
                        JSONValue parseLenient = JSONParser.parseLenient(resource.getText());
                        JSONTokenParser parser = new JSONTokenParser();
                        keyWords = parser.getTokens(parseLenient.isArray());
                        doAutocomplete(editor.getLineText(currentLine), editor.getCursorColumn(), tokenList, currentToken);
                    }

                    @Override
                    public void onError(ResourceException e) {
                        Log.info(e.getMessage());
                    }
                });
            } catch (ResourceException e) {
                Log.info(e.getMessage());
            }
            return;
        }
        doAutocomplete(editor.getLineText(currentLine), editor.getCursorColumn(), tokenList, currentToken);
    }

    /**
     * Do autocompletion
     *
     * @param lineContent
     * @param cursorPositionX
     * @param tokenList
     * @param currentToken
     */
    private void doAutocomplete(String lineContent, int cursorPositionX, List<Token> tokenList, Token currentToken) {
        try {
            List<Token> tokensFromParser = filterPhpTokens(tokenList);
            List<Token> tokens = new ArrayList<Token>();
            parseTokenLine(lineContent, cursorPositionX);
            // object
            String trimBeforeToken = beforeToken.trim();

            if (trimBeforeToken.endsWith("->")) {
                if (currentToken != null) {
                    String varType = "";
                    if (currentToken.hasProperty(TokenProperties.ELEMENT_TYPE))
                        varType = currentToken.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
                    else
                        varType = currentToken.getName();
                    Token type = findTokenForType(varType, tokensFromParser);
                    if (type != null) {
                        // in case $this-> reference
                        if (currentToken.getName().equals("$this")) {
                            addAllNotStaticMethodsAndFields(type, tokens);
                        } else {
                            addPublicMethodsAndFields(type, tokens);
                        }
                    }
                }
            }
            // class
            else if (trimBeforeToken.endsWith("::")) {
                String varType = "";
                if (currentToken.hasProperty(TokenProperties.ELEMENT_TYPE))
                    varType = currentToken.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
                else
                    varType = currentToken.getName();

                Token type = findTokenForType(varType, tokensFromParser);
                if (type != null) {
                    // in case self:: reference
                    if (currentToken.getName().equals("self")) {
                        addAllStaticFieldsAndMethod(type, tokens);
                    } else {
                        addClassVarAndConst(type, tokens);
                    }
                }
            }
            // other
            else {
                switch (currentToken.getType()) {
                    case FUNCTION:
                    case METHOD:
                        addTokensForFunction(currentToken, tokens);
                        break;

                    case PHP_TAG:
                        addTokensFromRootScript(tokensFromParser, tokens);
                        break;
                }
                tokens.addAll(keyWords);
            }

            Collections.sort(tokens, this);
            openForm(tokens, new PhpTokenWidgetFactory(), this);
        } catch (Exception e) {
            Log.info(e.getMessage());
        }
    }

    /**
     * Add root PHP variables and constants
     *
     * @param tokensFromParser
     * @param tokens
     */
    private void addTokensFromRootScript(List<Token> tokensFromParser, List<Token> tokens) {
        for (Token t : tokensFromParser) {
            if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numericValue().intValue() <= currentLine)
                tokens.add(t);
        }
    }

    /**
     * Add tokens for function or method (parameters and local variables)
     *
     * @param functionToken
     *         that represent function or method
     * @param tokens
     */
    private void addTokensForFunction(Token functionToken, List<Token> tokens) {
        if (functionToken.hasProperty(TokenProperties.PARAMETERS)) {
            for (Token t : functionToken.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue()) {
                tokens.add(t);
            }
        }

        if (functionToken.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
            for (Token t : functionToken.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
                if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numericValue().intValue() <= currentLine)
                    tokens.add(t);
            }
        }
    }

    /**
     * Add all static fields and methods declared in PHP class, also add class constants
     *
     * @param type
     *         that represent PHP class
     * @param tokens
     */
    @SuppressWarnings("unchecked")
    private void addAllStaticFieldsAndMethod(Token type, List<Token> tokens) {
        if (!type.hasProperty(TokenProperties.SUB_TOKEN_LIST))
            return;

        boolean add = false;
        for (Token t : type.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
            if (t.getType() == TokenType.CLASS_CONSTANT) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
                continue;
            }

            // add all static fields and methods
            if (t.hasProperty(TokenProperties.MODIFIERS)) {
                Object object = t.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue();
                List<Modifier> mod = (List<Modifier>)object;
                if (mod.contains(Modifier.STATIC))
                    add = true;
            }

            if ((t.getType() == TokenType.METHOD || t.getType() == TokenType.PROPERTY) && add) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
                add = false;
            }
        }
    }

    /**
     * Add all non static method and fields declared in PHP class
     *
     * @param type
     *         that represent PHP class
     * @param tokens
     */
    @SuppressWarnings("unchecked")
    private void addAllNotStaticMethodsAndFields(Token type, List<Token> tokens) {

        if (!type.hasProperty(TokenProperties.SUB_TOKEN_LIST))
            return;
        for (Token t : type.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
            if (t.hasProperty(TokenProperties.MODIFIERS)) {
                Object object = t.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue();
                List<Modifier> mod = (List<Modifier>)object;
                if (mod.contains(Modifier.STATIC))
                    continue;
            }
            // to avoid add constant token
            if (t.getType() == TokenType.METHOD || t.getType() == TokenType.PROPERTY) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
            }
        }
    }

    /**
     * Add public static field and methods
     *
     * @param type
     *         that represent PHP class
     * @param tokens
     */
    @SuppressWarnings("unchecked")
    private void addClassVarAndConst(Token type, List<Token> tokens) {
        if (!type.hasProperty(TokenProperties.SUB_TOKEN_LIST))
            return;

        boolean add = false;
        for (Token t : type.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
            if (t.getType() == TokenType.CLASS_CONSTANT) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
                continue;
            }

            // add only static public fields and methods
            if (t.hasProperty(TokenProperties.MODIFIERS)) {
                Object object = t.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue();
                List<Modifier> mod = (List<Modifier>)object;
                if (mod.contains(Modifier.STATIC) && (!mod.contains(Modifier.PRIVATE) || !mod.contains(Modifier.PROTECTED)))
                    add = true;
            }

            if ((t.getType() == TokenType.METHOD || t.getType() == TokenType.PROPERTY) && add) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
                add = false;
            }
        }

    }

    /**
     * @param type
     * @param tokens
     */
    @SuppressWarnings("unchecked")
    private void addPublicMethodsAndFields(Token type, List<Token> tokens) {
        if (!type.hasProperty(TokenProperties.SUB_TOKEN_LIST))
            return;

        boolean add = false;
        for (Token t : type.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue()) {
            // add only public fields and methods
            if (t.hasProperty(TokenProperties.MODIFIERS)) {
                Object object = t.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue();
                List<Modifier> mod = (List<Modifier>)object;
                if (mod.contains(Modifier.STATIC))
                    continue;

                if (mod.contains(Modifier.PUBLIC))
                    add = true;
            } else
                add = true;
            if ((t.getType() == TokenType.METHOD || t.getType() == TokenType.PROPERTY) && add) {
                t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(type.getName()));
                tokens.add(t);
                add = false;
            }
        }
    }

    /**
     * @param varType
     * @param tokensFromParser
     * @return
     */
    @SuppressWarnings("unchecked")
    private Token findTokenForType(String varType, List<Token> tokensFromParser) {
        for (Token t : tokensFromParser) {
            if (t.getName().equals(varType)) {
                return t;
            }
            if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
                Token tt =
                        findTokenForType(varType, (List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty()
                                                                .arrayValue());
                if (tt != null)
                    return tt;
            }
        }
        return null;
    }

    /**
     * Parse line received from editor, to separate text part needed for completion
     *
     * @param line
     *         text line received from editor
     * @param cursorPos
     *         position of cursor in line
     */
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
                case '.':
                case '(':
                case ')':
                case '{':
                case '}':
                case ';':
                case '[':
                case ']':
                case '\'':
                case ',':
                case '/':
                case '+':
                case '-':
                case '>':
                case '<':
                case '^':
                case ':':
                case '|':
                case '!':
                case '~':
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

    /**
     * Filter tokens received from editor, remove non php tokens(html,JavaScript etc);
     *
     * @param tokens
     *         received from parser
     * @return list of php tokens
     */
    @SuppressWarnings("unchecked")
    private List<Token> filterPhpTokens(List<Token> tokens) {
        List<Token> phpTokens = new ArrayList<Token>();
        for (Token t : tokens) {
            if (t.getType() == TokenType.PHP_TAG) {
                if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
                    phpTokens.addAll(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
                }
                continue;
            } else if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)) {
                phpTokens.addAll(filterPhpTokens((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST)
                                                               .isArrayProperty().arrayValue()));
            }
        }
        return phpTokens;
    }

    /**
     * This is debug function Print debug token tree
     *
     * @param tokens
     *         tree of the tokens
     * @param i
     */
    @SuppressWarnings("unused")
    private void printTokens(List<? extends Token> tokens, int i) {
        String spacer = "";
        for (int j = 0; j < i; j++) {
            spacer += " ";
        }
        i += 3;
        for (Token t : tokens) {
            if (t.getName() == null) {
                continue;
            }

            if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
                && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null) {
                printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i);
            }
        }
    }

    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(Token t1, Token t2) {
        if (t1.getType() == t2.getType()) {
            return t1.getName().compareTo(t2.getName());
        }

        if ((t1.getType() == TokenType.PARAMETER && t2.getType() == TokenType.VARIABLE)
            || (t1.getType() == TokenType.VARIABLE && t2.getType() == TokenType.PARAMETER)) {
            return t1.getName().compareTo(t2.getName());
        }

        if (t2.getType() == TokenType.PARAMETER) {
            return 1;
        }
        if (t1.getType() == TokenType.PARAMETER) {
            return -1;
        }

        if (t2.getType() == TokenType.VARIABLE) {
            return 1;
        }
        if (t1.getType() == TokenType.VARIABLE) {
            return -1;
        }
        if (t1.getType() == TokenType.PROPERTY) {
            return -1;
        }

        if (t2.getType() == TokenType.PROPERTY) {
            return 1;
        }

        if (t1.getType() == TokenType.CLASS_CONSTANT) {
            return -1;
        }

        if (t2.getType() == TokenType.CLASS_CONSTANT) {
            return 1;
        }

        if (t1.getType() == TokenType.CONSTANT) {
            return -1;
        }

        if (t2.getType() == TokenType.CONSTANT) {
            return 1;
        }

        if (t1.getType() == TokenType.METHOD || t1.getType() == TokenType.PROPERTY) {
            return -1;
        }

        if (t2.getType() == TokenType.METHOD || t2.getType() == TokenType.PROPERTY) {
            return 1;
        }

        return t1.getName().compareTo(t2.getName());
    }
}
