/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.util.ImageFactory;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IconButton extends Composite implements HasClickHandlers {

    /** Styles for component. */
    interface Style extends CssResource {
        String exoIconButtonPanelDown();

        String exoIconButtonPanelSelectedOver();

        String exoIconButtonPanelSelectedDown();

        String exoIconButtonIcon();

        String exoIconButtonPanelOver();

        String exoIconButtonPanelSelected();

        String exoIconButtonPanel();

    }

    interface Resorces extends ClientBundle {

        @Source("org/exoplatform/gwtframework/ui/client/component/icon-button/icon-button.css")
        Style css();

        @Source("org/exoplatform/gwtframework/ui/client/component/icon-button/button-background.png")
        ImageResource buttonBackground();
    }

    private static final Resorces RESORCES         = GWT.create(Resorces.class);

    static {
        RESORCES.css().ensureInjected();
    }

    /** Button's panel. */
    private ButtonPanel           buttonPanel;

    /**
     * Icon for enabled state. Icon must be represented as HTML string. e.g. <img src="..." />
     */
    protected String              icon;

    /**
     * Icon for disabled state. Icon must be represented as HTML string. e.g. <img src="..." />
     */
    protected String              disabledIcon;

    /** Command which will be executed when button was pressed. */
    protected Command             command;

    /** Is enabled. */
    private boolean               enabled          = true;

    /** Is button selected. */
    private boolean               selected         = false;

    private boolean               handleMouseEvent = true;

    private List<ClickHandler>    clickHandlers    = new ArrayList<ClickHandler>();

    /**
     * Create IconButton with icons. Icons is an HTML image and must be prepared like "<img ... />" tag Use
     * org.exoplatform.gwtframework.ui.client.util.ImageHelper to create Image from some sources.
     * 
     * @param icon icon for enabled state
     * @param disabledIcon icon for disabled state
     */
    public IconButton(String icon, String disabledIcon) {
        this(icon, disabledIcon, null);
    }

    /** Creates new instance of this {@link IconButton}. */
    public IconButton() {
        buttonPanel = new ButtonPanel();
        initWidget(buttonPanel);
        buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanel());
        setEnabled(true);
    }

    /**
     * Creates a new instance of this {@link IconButton}
     * 
     * @param icon html image for enabled state
     * @param disabledIcon html image for disabled state
     */
    public IconButton(Image icon, Image disabledIcon) {
        this(ImageHelper.getImageHTML(icon), ImageHelper.getImageHTML(disabledIcon), null);
    }

    /**
     * Create IconButton with icons and command. Icons is an HTML image and must be prepared like "<img ... />" tag Use
     * org.exoplatform.gwtframework.ui.client.util.ImageHelper to create Image from some sources.
     * 
     * @param icon icon for enabled state
     * @param disabledIcon icon for disabled state
     * @param command command which will be executed when button was pressed.
     */
    public IconButton(String icon, String disabledIcon, Command command) {
        this();

        this.icon = icon;
        this.disabledIcon = disabledIcon;
        this.command = command;

        renderIcon();
    }

    /**
     * Get command which will be executed when button was pressed.
     * 
     * @return command which will be executed when button was pressed
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Set command which will be executed when button was pressed.
     * 
     * @param command command which will be executed when button was pressed
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * Get is enabled.
     * 
     * @return is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set is enabled.
     * 
     * @param enabled is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanel());
        DOM.setElementAttribute(buttonPanel.getElement(), "enabled", "" + enabled);
        renderIcon();
    }

    /**
     * Get icon for enabled state.
     * 
     * @return icon which is uses for enabled state
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Set icon as HTML image for enabled state. Image must be prepared like "<img ... />" tag Use
     * org.exoplatform.gwtframework.ui.client.util.ImageHelper for creation Image from some sources.
     * 
     * @param icon icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
        if (enabled) {
            renderIcon();
        }
    }

    /**
     * Get icon for disabled state.
     * 
     * @return icon which is uses for disabled state
     */
    public String getDisabledIcon() {
        return disabledIcon;
    }

    /**
     * Set icon as HTML image for disabled state. Image must be prepared like "<img ... />" tag Use
     * org.exoplatform.gwtframework.ui.client.util.ImageHelper for creation Image from some sources.
     * 
     * @param disabledIcon icon which is uses for disabled state
     */
    public void setDisabledIcon(String disabledIcon) {
        this.disabledIcon = disabledIcon;
        if (enabled) {
            return;
        }

        renderIcon();
    }

    /**
     * Get is button selected.
     * 
     * @return <code>true</code> if button selected, <code>false</code> otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set button is selected.
     * 
     * @param selected is button selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelSelected());
        } else {
            buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanel());
        }
    }

    /** Redraw icon. */
    private void renderIcon() {
        if (enabled) {
            if (icon != null) {
                buttonPanel.getElement().setInnerHTML(icon);
            } else {
                buttonPanel.getElement().setInnerHTML("");
                return;
            }
        } else {
            if (disabledIcon != null) {
                buttonPanel.getElement().setInnerHTML(disabledIcon);
            } else {
                buttonPanel.getElement().setInnerHTML("");
                return;
            }

        }

        Element e = buttonPanel.getElement();
        Element imageElement = DOM.getChild(e, 0);
        // NOT Work in IE !!!
        // DOM.setElementAttribute(imageElement, "class", Style.BUTTON_ICON);
        imageElement.setClassName(RESORCES.css().exoIconButtonIcon());
    }

    /**
     * Set name of the image which will be received from {@link ImageFactory}
     * 
     * @param imageName name of the image
     */
    public void setImageName(String imageName) {
        icon = ImageHelper.getImageHTML(ImageFactory.getImage(imageName));
        disabledIcon = ImageHelper.getImageHTML(ImageFactory.getDisabledImage(imageName));
        renderIcon();
    }

    /** Mouse Over handler. */
    private void onMouseOver() {
        if (handleMouseEvent)
        {
            if (selected) {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelSelectedOver());
            } else {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelOver());
            }
        }
    }

    /** Mouse Out handler. */
    private void onMouseOut() {
        if (handleMouseEvent)
        {
            if (selected) {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelSelected());
            } else {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanel());
            }
        }
    }

    /** Mouse Down handler. */
    private void onMouseDown() {
        if (handleMouseEvent)
        {
            if (selected) {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelSelectedDown());
            } else {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelDown());
            }
        }
    }

    /** Mouse Up handler. */
    private void onMouseUp() {
        if (handleMouseEvent)
        {
            if (selected) {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelSelectedOver());
            } else {
                buttonPanel.setStyleName(RESORCES.css().exoIconButtonPanelOver());
            }
        }
    }

    /** Mouse Click handler. */
    private void onClick() {
        if (command != null) {
            command.execute();
        }

        ClickEvent clickEvent = new ClickEvent() {
        };
        for (ClickHandler clickHandler : clickHandlers) {
            clickHandler.onClick(clickEvent);
        }
    }

    /** ButtonPanel class uses for listening mouse events on button. */
    private class ButtonPanel extends FlowPanel {

        public ButtonPanel() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONCLICK);
        }

        /** Handle browser's events. */
        @Override
        public void onBrowserEvent(Event event) {
            if (!enabled) {
                return;
            }

            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    onMouseOver();
                    break;

                case Event.ONMOUSEOUT:
                    onMouseOut();
                    break;

                case Event.ONMOUSEDOWN:
                    if (event.getButton() != Event.BUTTON_LEFT) {
                        return;
                    }

                    onMouseDown();
                    break;

                case Event.ONMOUSEUP:
                    onMouseUp();
                    break;

                case Event.ONCLICK:
                    onClick();
                    break;
            }
        }

    }

    /**
     * Sets the title associated with this button. The title is the 'tool-tip' displayed to users when they hover over the object.
     * 
     * @param title the object's new title
     */
    public void setTitle(String title) {
        buttonPanel.setTitle(title);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        clickHandlers.add(handler);
        return new ClickHandlerRegistration(handler);
    }

    public boolean isHandleMouseEvent() {
        return handleMouseEvent;
    }

    public void setHandleMouseEvent(boolean handleMouseEvent) {
        this.handleMouseEvent = handleMouseEvent;
    }

    private class ClickHandlerRegistration implements HandlerRegistration {

        private ClickHandler clickHandler;

        public ClickHandlerRegistration(ClickHandler clickHandler) {
            this.clickHandler = clickHandler;
        }

        @Override
        public void removeHandler() {
            clickHandlers.remove(clickHandler);
        }

    }

}
