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
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JsWodget Feb 24, 2011 4:22:18 PM evgen $
 */
public class JsWidget extends JSBaseWidget {

    /** @param token */
    public JsWidget(Token token) {
        super(token);
        grid = new Grid(1, 3);
        grid.setStyleName(JavaScriptEditorExtension.RESOURCES.css().item());
        grid.setWidth("100%");

        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        String name = token.getName();
        if (token.getType() == TokenType.FUNCTION) {
            name += "()";
        }
        if (token.hasProperty(TokenProperties.SHORT_HINT)) {
            name += token.getProperty(TokenProperties.SHORT_HINT).isStringProperty().stringValue();
        }

        Label nameLabel = new Label(name, false);

        grid.setWidget(0, 1, nameLabel);
        String pack = "";
        if (token.hasProperty(TokenProperties.FQN)) {
            pack = "-" + token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
        }

        Label l = new Label(pack, false);
        l.setStyleName(JavaScriptEditorExtension.RESOURCES.css().fqn());
        grid.setWidget(0, 2, l);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }

    /**
     * Image that represent current token type(Class, Interface or Annotation)
     *
     * @return {@link Image}
     */
    private Image getImage() {
        switch (token.getType()) {
            case METHOD:
                return new Image(JavaScriptEditorExtension.RESOURCES.methodItem());
            case VARIABLE:
                return new Image(JavaScriptEditorExtension.RESOURCES.varItem());
            case PROPERTY:
                return new Image(JavaScriptEditorExtension.RESOURCES.propertyItem());
            case FUNCTION:
                return new Image(JavaScriptEditorExtension.RESOURCES.functionItem());
            default:
                return new Image(JavaScriptEditorExtension.RESOURCES.template());

        }

    }
}
