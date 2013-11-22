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
package org.exoplatform.ide.editor.javascript.client;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.json.client.JsoArray;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.google.collide.client.documentparser.ExternalParser;
import com.google.gwt.core.client.JavaScriptException;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.javascript.client.syntaxvalidator.JsToken;

import static org.exoplatform.gwtframework.commons.rest.MimeType.APPLICATION_JAVASCRIPT;

/**
 * Parser implementation that used Esprima to extract tokens.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: JavaScriptExternalParser.java May 29, 2013 12:01:10 PM azatsarynnyy $
 */
public class JavaScriptExternalParser implements ExternalParser {

    /**
     * @see com.google.collide.client.documentparser.ExternalParser#getTokenList(String)
     */
    @Override
    public JsonArray< ? extends Token> getTokenList(String content) {
        JsonArray<Token> tokensArray = JsonCollections.createArray();
        try {
            JsonArray<JsToken> jsTokens = doParse(content);
            for (JsToken jsToken : jsTokens.asIterable()) {
                TokenBeenImpl token = convertToken(jsToken);
                if (token != null) {
                    tokensArray.add(token);
                }
            }
        } catch (JavaScriptException e) {
            Log.error(getClass(), e);
        }

        return tokensArray;
    }

    private TokenBeenImpl convertToken(JsToken jsToken) {
        TokenBeenImpl token = null;
        if ("VariableDeclaration".equals(jsToken.getType())) {
            token = new TokenBeenImpl(jsToken.getName(), TokenType.VARIABLE, jsToken.getLineNumber(), APPLICATION_JAVASCRIPT);
        } else if ("VariableDeclarator".equals(jsToken.getType())) {
            token = new TokenBeenImpl(jsToken.getName(), TokenType.VARIABLE, jsToken.getLineNumber(), APPLICATION_JAVASCRIPT);
        } else if ("Property".equals(jsToken.getType())) {
            token = new TokenBeenImpl(jsToken.getName(), TokenType.PROPERTY, jsToken.getLineNumber(), APPLICATION_JAVASCRIPT);
        } else if ("FunctionDeclaration".equals(jsToken.getType())) {
            token = new TokenBeenImpl(jsToken.getName(), TokenType.FUNCTION, jsToken.getLineNumber(), APPLICATION_JAVASCRIPT);

            JsoArray<JsToken> params = jsToken.getParams();
            for (JsToken param : params.asIterable()) {
                TokenBeenImpl parameter = new TokenBeenImpl(param.getName(), TokenType.PARAMETER, param.getLineNumber(),
                                                            APPLICATION_JAVASCRIPT, param.getName());
                token.addParameter(parameter);
            }
        } else {
            // unknown token type
            return null;
        }

        JsoArray<JsToken> subTokens = jsToken.getSubTokens();
        if (subTokens != null) {
            for (JsToken childJsToken : subTokens.asIterable()) {
                TokenBeenImpl childToken = convertToken(childJsToken);
                if (childToken != null) {
                    if ((childToken.getType() == TokenType.VARIABLE) && (childToken.getSubTokenList() != null) &&
                        (childToken.getSubTokenList().size() > 1)) {
                        token.setSubTokenList(childToken.getSubTokenList());
                    } else {
                        token.addSubToken(childToken);
                    }
                }
            }
        }

        return token;
    }

    private native JsoArray<JsToken> doParse(String content)
    /*-{
        return $wnd.esprima.parse(content, {tolerant: true, loc: true}).body;
    }-*/;
}
