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

import com.google.gwt.user.client.ui.*;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.codeassistant.ModifierHelper;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 9:40:49 AM evgen $
 */
public class JavaConstructorWidget extends JavaTokenWidgetBase {

    private Grid grid;

    private String value;

    /** @param token */
    public JavaConstructorWidget(Token token, String restContext, String projectId) {
        super(token, restContext, projectId);

        grid = new Grid(1, 3);
        grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
        grid.setWidth("100%");

        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        value = token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
        Label nameLabel = new Label(value, false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
        grid.setWidget(0, 1, nameLabel);

        Label label = new Label("-" + token.getProperty(TokenProperties.DECLARING_CLASS), false);
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

    /**
     * Return {@link Image} for specific access modifier
     *
     * @return
     */
    private Image getImage() {
        Image i;
        if (ModifierHelper.isPrivate(modifieres)) {
            i = new Image(JavaClientBundle.INSTANCE.publicField());
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
        return value;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        return null;
    }

}
