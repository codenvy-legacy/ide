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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.*;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;
import org.exoplatform.ide.editor.html.client.codeassistant.HtmlCodeAssistant;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JapHtmlCodeAssistant May 5, 2011 4:02:40 PM evgen $
 */
class JspHtmlCodeAssistant extends HtmlCodeAssistant {

    interface JspHtmlBuandle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/jsp/client/tokens/jsp_tags.js")
        ExternalTextResource jspTagsTokens();
    }

    private List<Token> templates;

    /** @see org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant#getTokens(java.lang.String, int) */
    @Override
    protected void getTokens(final String lineContent, final int cursorPositionX) throws ResourceException {
        // if htmlTokens already fill call method from super class
        if (htmlTokens.size() > 0) {
            super.getTokens(lineContent, cursorPositionX);
            return;
        }

        JspHtmlBuandle buandle = GWT.create(JspHtmlBuandle.class);
        buandle.jspTagsTokens().getText(new ResourceCallback<TextResource>() {

            @Override
            public void onSuccess(TextResource resource) {
                try {
                    JSONTokenParser parser = new JSONTokenParser();
                    JavaScriptObject o = parseJson(resource.getText());
                    JSONObject obj = new JSONObject(o);
                    List<Token> objects = parser.getTokens(obj.get("jsp_tags").isArray());
                    templates = parser.getTokens(obj.get("jsp_templates").isArray());
                    for (Token t : objects) {
                        htmlTokens.add(t);
                        noBaseEvents.add(t.getName());
                        noCoreAttributes.add(t.getName());
                    }
                    noEndTag.add("jsp:setProperty");
                    noEndTag.add("jsp:getProperty");
                    noEndTag.add("jsp:param");
                    noEndTag.add("jsp:invoke");
                    JspHtmlCodeAssistant.this.getTokens(lineContent, cursorPositionX);
                } catch (ResourceException e) {
                    Log.info(e.getMessage());
                }
            }

            @Override
            public void onError(ResourceException e) {
                try {
                    JspHtmlCodeAssistant.this.getTokens(lineContent, cursorPositionX);
                } catch (ResourceException e1) {
                    Log.info(e1.getMessage());
                }
            }
        });

    }

    /** @see org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant#showDefaultTags(java.util.List) */
    @Override
    protected void showDefaultTags(List<Token> token) {
        if (templates != null)
            token.addAll(templates);
        super.showDefaultTags(token);
    }

}
