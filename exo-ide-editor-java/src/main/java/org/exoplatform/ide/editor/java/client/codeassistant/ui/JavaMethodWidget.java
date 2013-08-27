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
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 10:06:35 AM evgen $
 */
public class JavaMethodWidget extends JavaTokenWidgetBase {

    private Grid grid;

    /**
     * @param token
     * @param restContext
     */
    public JavaMethodWidget(Token token, String restContext, String projectId) {
        super(token, restContext, projectId);
        grid = new Grid(1, 3);
        grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
        // grid.setWidth("100%");

        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        String name = token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
        if (token.hasProperty(TokenProperties.RETURN_TYPE))
            name += ":" + token.getProperty(TokenProperties.RETURN_TYPE).isStringProperty().stringValue();
        else
            name += ":" + token.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();

        Label nameLabel = new Label(name, false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
        grid.setWidget(0, 1, nameLabel);

        String pack = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
        Label label = new Label("-" + pack, false);
        label.setStyleName(JavaClientBundle.INSTANCE.css().fqnStyle());
        grid.setWidget(0, 2, label);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }


    private Image getImage() {

        if (ModifierHelper.isPrivate(modifieres)) {
            return new Image(JavaClientBundle.INSTANCE.privateMethod());
        } else if (ModifierHelper.isProtected(modifieres)) {
            return new Image(JavaClientBundle.INSTANCE.protectedMethod());
        } else if (ModifierHelper.isPublic(modifieres)) {
            return new Image(JavaClientBundle.INSTANCE.publicMethod());
        } else {
            return new Image(JavaClientBundle.INSTANCE.defaultMethod());
        }

    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        return token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
    }

}
