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
package org.exoplatform.ide.editor.javascript.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JSBaseWidget Feb 24, 2011 11:56:26 AM evgen $
 */
public abstract class JSBaseWidget extends TokenWidget {

    protected Grid grid;

    /** @param token */
    public JSBaseWidget(Token token) {
        super(token);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        if (token.hasProperty(TokenProperties.FULL_TEXT)) {
            Widget w = new SimplePanel();
            w.getElement().setInnerHTML(token.getProperty(TokenProperties.FULL_TEXT).isStringProperty().stringValue());
            return w;
        }
        return null;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName() */
    @Override
    public String getTokenName() {
        return getToken().getName();
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle() */
    @Override
    public void setSelectedStyle() {
        setStyleName(JavaScriptEditorExtension.RESOURCES.css().selectedItem());
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle() */
    @Override
    public void setDefaultStyle() {
        setStyleName(JavaScriptEditorExtension.RESOURCES.css().item());
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        if (token.hasProperty(TokenProperties.CODE))
            return token.getProperty(TokenProperties.CODE).isStringProperty().stringValue();
        else
            return token.getName();
    }

}
