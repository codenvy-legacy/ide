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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JsKeyWordWidget Feb 24, 2011 11:58:25 AM evgen $
 */
public class JsKeyWordWidget extends JSBaseWidget {

    /** @param token */
    public JsKeyWordWidget(Token token) {
        super(token);
        grid = new Grid(1, 2);
        grid.setStyleName(JavaScriptEditorExtension.RESOURCES.css().item());
        grid.setWidth("100%");
        Image i = new Image(JavaScriptEditorExtension.RESOURCES.blankImage());
        i.setHeight("16px");

        grid.setWidget(0, 0, i);

        Label nameLabel = new Label(token.getName(), false);
        // nameLabel.setStyleName(GroovyPluginImageBundle.INSTANCE.css().keywordStyle());
        grid.setWidget(0, 1, nameLabel);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);

        initWidget(grid);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        return token.getName();
    }

}
