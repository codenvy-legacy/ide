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
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 13, 2010 4:06:10 PM evgen $
 */
public class JavaVariableWidget extends JavaTokenWidgetBase {

    private Grid grid;

    /**
     * @param token
     * @param restContext
     */
    public JavaVariableWidget(Token token) {
        super(token, "", "");
        grid = new Grid(1, 2);
        grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
        Image i = new Image(JavaClientBundle.INSTANCE.variable());
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        String name =
                token.getName() + ":" + token.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
        Label nameLabel = new Label(name, false);
        grid.setWidget(0, 1, nameLabel);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 1, "100%");

        initWidget(grid);
        setWidth("100%");
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        return token.getName();
    }

    /** @see org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaTokenWidgetBase.module.groovy.codeassistant.ui
     * .GroovyTokenWidgetBase#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        if (token.hasProperty(TokenProperties.FULL_TEXT)) {
            Widget w = new SimplePanel();
            w.getElement().setInnerHTML(token.getProperty(TokenProperties.FULL_TEXT).isStringProperty().stringValue());
            return w;
        }
        return null;
    }

}
