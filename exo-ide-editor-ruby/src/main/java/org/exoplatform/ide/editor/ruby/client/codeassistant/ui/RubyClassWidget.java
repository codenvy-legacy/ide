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
package org.exoplatform.ide.editor.ruby.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.ruby.client.RubyClientBundle;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyClassWidget Apr 29, 2011 4:09:20 PM evgen $
 */
public class RubyClassWidget extends RubyBaseWidget {

    /** @param token */
    public RubyClassWidget(Token token) {
        super(token);

        grid = new Grid(1, 3);
        grid.setStyleName(RubyClientBundle.INSTANCE.css().item());
        grid.setWidth("100%");

        Image i = new Image(RubyClientBundle.INSTANCE.classItem());
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        Label nameLabel = new Label(token.getName(), false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());

        grid.setWidget(0, 1, nameLabel);

        if (token.hasProperty(TokenProperties.FQN)) {
            String pack = token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
            Label l = new Label("-" + pack, false);
            l.setStyleName(RubyClientBundle.INSTANCE.css().fqnStyle());
            grid.setWidget(0, 2, l);
        }
        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 2, "100%");

        initWidget(grid);
        setWidth("100%");
    }

}
