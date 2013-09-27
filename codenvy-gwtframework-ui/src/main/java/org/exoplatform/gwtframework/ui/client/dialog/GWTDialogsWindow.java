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
package org.exoplatform.gwtframework.ui.client.dialog;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.window.Window;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 16, 2011 12:30:27 PM anya $
 */
public class GWTDialogsWindow extends Window {

    private final String BUTTONS_PANEL_HEIGHT = "22px";

    private VerticalPanel mainLayout;

    private HorizontalPanel buttonsLayout;

    /**
     *
     */
    public GWTDialogsWindow(String id, String title, int width, int height, Widget content) {
        super(title);
        getElement().setId(id);
        setModal(true);
        setHeight(height);
        setWidth(width);

        mainLayout = new VerticalPanel();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainLayout.setSpacing(10);

        mainLayout.add(content);

        buttonsLayout = createButtonsLayout();
        mainLayout.add(buttonsLayout);

        setWidget(mainLayout);
        //      center();
    }

    /**
     * Create layout for displaying buttons.
     *
     * @return {@link HorizontalPanel} layout for buttons
     */
    public HorizontalPanel createButtonsLayout() {
        HorizontalPanel buttonsLayout = new HorizontalPanel();
        buttonsLayout.setHeight(BUTTONS_PANEL_HEIGHT);
        buttonsLayout.setSpacing(5);
        buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        return buttonsLayout;
    }

    /** @return the mainLayout */
    public VerticalPanel getMainLayout() {
        return mainLayout;
    }

    /** @return the buttonsLayout */
    public HorizontalPanel getButtonsLayout() {
        return buttonsLayout;
    }

}
