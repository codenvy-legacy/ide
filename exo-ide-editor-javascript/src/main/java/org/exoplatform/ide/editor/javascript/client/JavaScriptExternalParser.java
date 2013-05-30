/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.javascript.client;

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
            // ignore
        }

        return tokensArray;
    }

    private TokenBeenImpl convertToken(JsToken jsToken) {
        TokenBeenImpl token = null;
        if ("VariableDeclaration".equals(jsToken.getType())) {
            token = new TokenBeenImpl(jsToken.getName(), TokenType.VARIABLE, jsToken.getLineNumber(), APPLICATION_JAVASCRIPT);
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

        JsoArray<JsToken> body = jsToken.getBody();
        if (body != null) {
            for (JsToken childJsToken : body.asIterable()) {
                TokenBeenImpl childToken = convertToken(childJsToken);
                if (childToken != null) {
                    token.addSubToken(childToken);
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
