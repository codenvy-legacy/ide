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
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 5:00:40 PM evgen $
 */
public class JavaClassTokenWidget extends JavaTokenWidgetBase {

    private Grid grid;

    /**
     * @param token
     * @param number
     */
    public JavaClassTokenWidget(Token token, String restContext, String projectId) {
        super(token, restContext, projectId);
        grid = new Grid(1, 3);
        grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
        grid.setWidth("100%");

        Image i = getImage();
        i.setHeight("16px");
        grid.setWidget(0, 0, i);

        Label nameLabel = new Label(token.getName(), false);
        nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());

        grid.setWidget(0, 1, nameLabel);

        String pack = token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
        if (pack.contains("."))
            pack = pack.substring(0, pack.lastIndexOf("."));
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

    /**
     * Image that represent current token type(Class, Interface or Annotation)
     *
     * @return {@link Image}
     */
    private Image getImage() {
        switch (token.getType()) {

            case INTERFACE:

                return new Image(JavaClientBundle.INSTANCE.interfaceItem());

            case ANNOTATION:
                return new Image(JavaClientBundle.INSTANCE.annotationItem());

            case ENUM:
                return new Image(JavaClientBundle.INSTANCE.enumItem());

            case CLASS:
            default:
                return new Image(JavaClientBundle.INSTANCE.classItem());

        }

    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        return token.getName();
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        return new Frame(docContext + token.getProperty(TokenProperties.FQN).isStringProperty().stringValue()
                         + "&projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=true");
    }

}
