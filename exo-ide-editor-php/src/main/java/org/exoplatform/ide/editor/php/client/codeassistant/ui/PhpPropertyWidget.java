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

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.php.client.PhpClientBundle;

/**
 * Ui component that represent PHP Class or Object property.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class PhpPropertyWidget extends PhpTokenWidgetBase {

    /** @param token */
    public PhpPropertyWidget(Token token) {
        super(token);
        grid = new Grid(1, 3);
        grid.setStyleName(PhpClientBundle.INSTANCE.css().item());
        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        String name = token.getName();
        if (token.hasProperty(TokenProperties.ELEMENT_TYPE)) {
            name += ":" + token.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
        }
        Label nameLabel = new Label(name, false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
        grid.setWidget(0, 1, nameLabel);

        String pack = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
        Label l = new Label("-" + pack, false);
        l.setStyleName(PhpClientBundle.INSTANCE.css().fqnStyle());

        grid.setWidget(0, 2, l);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }

    private Image getImage() {
        Image i;
        if (modifieres.contains(Modifier.PRIVATE)) {
            i = new Image(PhpClientBundle.INSTANCE.privateField());
        } else if (modifieres.contains(Modifier.PROTECTED)) {
            i = new Image(PhpClientBundle.INSTANCE.protectedField());
        } else {
            i = new Image(PhpClientBundle.INSTANCE.publicField());
        }

        return i;
    }

}
