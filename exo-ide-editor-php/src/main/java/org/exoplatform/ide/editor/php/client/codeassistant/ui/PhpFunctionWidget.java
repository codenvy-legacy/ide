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
package org.exoplatform.ide.editor.php.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.php.client.PhpClientBundle;

/**
 * Ui component that represent PHP function or class method.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class PhpFunctionWidget extends PhpTokenWidgetBase {

    /** @param token */
    public PhpFunctionWidget(Token token) {
        super(token);
        grid = new Grid(1, 3);
        grid.setStyleName(PhpClientBundle.INSTANCE.css().item());

        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        String name = token.getName();
        if (token.hasProperty(TokenProperties.PARAMETER_TYPES))
            name += token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
        else
            name += getParameters();

        Label nameLabel = new Label(name, false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
        grid.setWidget(0, 1, nameLabel);

        String pack = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
        Label label = new Label("-" + pack, false);
        label.setStyleName(PhpClientBundle.INSTANCE.css().fqnStyle());
        grid.setWidget(0, 2, label);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }

    /** @return  */
    private String getParameters() {
        String param = "(";
        if (token.hasProperty(TokenProperties.PARAMETERS)) {
            for (Token t : token.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue()) {
                param += t.getName() + ",";
            }
            if (param.endsWith(",")) {
                param = param.substring(0, param.length() - 1);
            }

        }
        return param += ")";
    }

    /** @see org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpTokenWidgetBase#getTokenValue() */
    @Override
    public String getTokenValue() {

        return token.getName() + getParameters();

    }

    private Image getImage() {
        return new Image(PhpClientBundle.INSTANCE.publicMethod());
    }

}
