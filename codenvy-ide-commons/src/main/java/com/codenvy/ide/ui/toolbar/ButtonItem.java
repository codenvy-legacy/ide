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
package com.codenvy.ide.ui.toolbar;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.ui.menu.Item;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Toolbar image button.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ButtonItem extends Composite implements ToolbarItem, MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler,
                                                     ClickHandler {

    protected static final ToolbarResources.Css css = Toolbar.RESOURCES.toolbar();
    /** Icon for button. */
    protected ImageResource image;
    /** Command which will be executed when button was pressed. */
    protected Command       command;
    private   String        tooltip;
    private   FlowPanel     panel;
    /** Is enabled. */
    private boolean enabled  = true;
    /** Is button selected. */
    private boolean selected = false;


    public ButtonItem(ImageResource image) {
        this(image, null);
    }

    public ButtonItem(ImageResource image, Command command) {
        this(image, command, null);
    }

    public ButtonItem(ImageResource image, Command command, String tooltip) {
        this.image = image;
        this.command = command;
        this.tooltip = tooltip;
        panel = new FlowPanel();
        initWidget(panel);
        panel.setStyleName(css.iconButtonPanel());
        renderImage();
        addDomHandlers();
        if (tooltip != null) {
            getElement().setAttribute("title", tooltip);
        }
    }

    private void addDomHandlers() {
        panel.addDomHandler(this, MouseOverEvent.getType());
        panel.addDomHandler(this, MouseOutEvent.getType());
        panel.addDomHandler(this, MouseDownEvent.getType());
        panel.addDomHandler(this, MouseUpEvent.getType());
        panel.addDomHandler(this, ClickEvent.getType());
    }

    /** Redraw icon. */
    private void renderImage() {
        panel.clear();
        if (image != null) {
            Image img = new Image(image);
            img.setStyleName(css.iconButtonIcon());
            panel.add(img);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Command getCommand() {
        return command;
    }

    /** {@inheritDoc} */
    @Override
    public String getHotKey() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getImage() {
        return image;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Item> getItems() {
        return JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            removeStyleName(css.disabled());
        } else {
            addStyleName(css.disabled());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelected());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }

    }

    /** Mouse Over handler. */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedOver());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }
    }

    /** Mouse Out handler. */
    @Override
    public void onMouseOut(MouseOutEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelected());
        } else {
            panel.setStyleName(css.iconButtonPanel());
        }
    }

    /** Mouse Down handler. */
    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedDown());
        } else {
            panel.setStyleName(css.iconButtonPanelDown());
        }
    }

    /** Mouse Up handler. */
    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (!enabled) {
            return;
        }
        if (selected) {
            panel.setStyleName(css.iconButtonPanelSelectedOver());
        } else {
            panel.setStyleName(css.iconButtonPanelOver());
        }
    }

    /** Mouse Click handler. */
    @Override
    public void onClick(ClickEvent event) {
        if (command != null && enabled) {
            command.execute();
        }
    }
}
