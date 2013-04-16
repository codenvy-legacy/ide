/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.part.view;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.part.PartStackUIResources;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class BaseView<T> extends Composite implements View<T> {

    protected DockLayoutPanel toolBar;
    protected DockLayoutPanel container;
    private final Button hideButton;

    public BaseView(PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        initWidget(container);
        setSize("100%", "100%");
        toolBar = new DockLayoutPanel(Style.Unit.PX);
        toolBar.addStyleName(resources.partStackCss().ideBasePartToolbar());
        container.addNorth(toolBar, 20);

        hideButton = new Button();
        hideButton.setIcon(IconType.MINUS);
        hideButton.setSize(ButtonSize.MINI);
        hideButton.setType(ButtonType.SUCCESS);
        hideButton.addStyleName(resources.partStackCss().ideButtonMicro());
        DropdownButton settings = new DropdownButton();
        settings.add(new NavLink("1"));
        settings.add(new NavLink("2"));
        settings.add(new NavLink("3"));
        settings.add(new NavLink("4"));
        settings.setIcon(IconType.TH_LARGE);
        settings.addStyleName(resources.partStackCss().ideButtonMicro());
        settings.setSize(ButtonSize.MINI);
        toolBar.addEast(hideButton, 20);
        toolBar.addEast(settings, 32);
    }

    public void setTitle(String title) {
        Label l = new Label(title, false);
        l.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        l.getElement().getStyle().setLineHeight(20, Style.Unit.PX);
        l.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        l.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        toolBar.addWest(l, 60);
    }


}