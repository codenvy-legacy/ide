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
package org.exoplatform.ide.editor.java.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.codeassistant.ModifierHelper;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 10:25:25 AM evgen $
 */
public class JavaFieldWidget extends JavaTokenWidgetBase {

    private Grid grid;

    /** @param token */
    public JavaFieldWidget(Token token, String restContext, String projectId) {
        super(token, restContext, projectId);
        grid = new Grid(1, 3);
        grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
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
        l.setStyleName(JavaClientBundle.INSTANCE.css().fqnStyle());

        grid.setWidget(0, 2, l);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }

    /** @return  */
    private Image getImage() {
        Image i;
        if (ModifierHelper.isPrivate(modifieres)) {
            i = new Image(JavaClientBundle.INSTANCE.privateField());
        } else if (ModifierHelper.isProtected(modifieres)) {
            i = new Image(JavaClientBundle.INSTANCE.protectedField());
        } else if (ModifierHelper.isPublic(modifieres)) {
            i = new Image(JavaClientBundle.INSTANCE.publicField());
        } else {
            i = new Image(JavaClientBundle.INSTANCE.defaultField());
        }
        return i;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        return getTokenName();
    }

}
