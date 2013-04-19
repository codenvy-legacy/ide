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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class BaseView<T extends BaseActionDelegate> extends Composite implements View<T> {

    protected DockLayoutPanel toolBar;
    protected DockLayoutPanel container;
    protected T               delegate;

    public BaseView(PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        initWidget(container);
        setSize("100%", "100%");
        toolBar = new DockLayoutPanel(Style.Unit.PX);
        toolBar.addStyleName(resources.partStackCss().ideBasePartToolbar());
        container.addNorth(toolBar, 20);
        ToolButton toolButton = new ToolButton(new Image(resources.minimize()));
        toolButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                minimize();
            }
        });
        toolBar.addEast(toolButton, 20);
    }

    protected void minimize() {
        if (delegate != null)
            delegate.minimize();
    }

    public void setTitle(String title) {
        Label l = new Label(title, false);
        l.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        l.getElement().getStyle().setLineHeight(20, Style.Unit.PX);
        l.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        l.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        toolBar.addWest(l, 60);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(T delegate) {
        this.delegate = delegate;
    }
}