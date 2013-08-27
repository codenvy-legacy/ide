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
package org.exoplatform.ide.editor.jsp.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.*;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JSPCodeAssistant Apr 15, 2011 12:34:45 PM evgen $
 */
public class JspCodeAssistant extends JavaCodeAssistant {

    public interface JspBundle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/jsp/client/tokens/jsp_tokens.js")
        ExternalTextResource jspImplicitObjects();
    }

    private static Map<String, Token> implicitObjects;

    private JspHtmlCodeAssistant jspHtmlCodeAssistant;

    /**
     * @param service
     * @param factory
     * @param errorHandler
     */
    public JspCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
                            JavaCodeAssistantErrorHandler errorHandler) {
        super(service, factory, errorHandler);
        jspHtmlCodeAssistant = new JspHtmlCodeAssistant();

        if (implicitObjects == null) {

            JspBundle bundle = GWT.create(JspBundle.class);
            try {
                bundle.jspImplicitObjects().getText(new ResourceCallback<TextResource>() {

                    @Override
                    public void onSuccess(TextResource resource) {
                        JSONTokenParser parser = new JSONTokenParser();
                        List<Token> objects = parser.getTokens(new JSONArray(parseJson(resource.getText())));
                        implicitObjects = new HashMap<String, Token>();
                        for (Token t : objects) {
                            implicitObjects.put(t.getName(), t);
                        }
                    }

                    @Override
                    public void onError(ResourceException e) {
                        Log.info(e.getMessage());
                    }
                });
            } catch (ResourceException e) {
                Log.info(e.getMessage());
            }
        }
    }

    /**
     * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.client
     * .api.Editor,
     *      java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String,
     *      org.exoplatform.ide.editor.api.codeassitant.Token)
     */
    @Override
    public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
                                   String lineMimeType, Token currentToken) {
        if (MimeType.APPLICATION_JAVA.equals(lineMimeType)) {
            super.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType, currentToken);
        } else {
            jspHtmlCodeAssistant.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType,
                                                    currentToken);
        }
    }

    /** @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#callOpenForm(java.util.List) */
    @Override
    protected void callOpenForm(List<Token> tokens) {
        if (action == Action.CLASS_NAME_AND_LOCAL_VAR || action == Action.LOCAL_VAR)
            tokens.addAll(implicitObjects.values());
        super.callOpenForm(tokens);
    }

    /**
     * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#showMethods(org.exoplatform.ide.editor.api
     * .codeassitant.Token,
     *      java.lang.String, java.lang.String)
     */
    @Override
    protected void showMethods(Token currentToken, String varToken) {
        if (implicitObjects.containsKey(varToken)) {
            action = Action.PUBLIC;
            curentFqn =
                    implicitObjects.get(varToken).getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
            getClassDescription();
        } else {
            super.showMethods(currentToken, varToken);
        }
    }
}
