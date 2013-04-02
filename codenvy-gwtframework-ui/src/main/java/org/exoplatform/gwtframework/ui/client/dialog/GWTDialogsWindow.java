/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
