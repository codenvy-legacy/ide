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
import com.codenvy.ide.ui.menu.CloseMenuHandler;
import com.codenvy.ide.ui.menu.Item;
import com.codenvy.ide.ui.menu.ItemSelectedHandler;
import com.codenvy.ide.ui.menu.MenuLockLayer;
import com.codenvy.ide.ui.menu.PopupMenu;
import com.codenvy.ide.ui.menu.PopupMenuItem;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PopupItem extends Composite implements ToolbarItem, CloseMenuHandler, ItemSelectedHandler {

    private static final ToolbarResources.Css css = Toolbar.RESOURCES.toolbar();

    /** Enabled state. True as default. */
    private boolean enabled = true;
    /** Icon for enabled state. */
    private ImageResource icon;
    /** Lock Layer uses for locking rest of the screen, which does not covered by Popup Menu. */
    private MenuLockLayer lockLayer;
    /** List of Popup Menu items. */
    private JsonArray<Item> menuItems = JsonCollections.createArray();
    /** Popup Menu button panel (<div> HTML element). */
    private ButtonPanel panel;
    /** Has instance if Popup Menu is opened. */
    private PopupMenu   popupMenu;

    /**
     * Create Popup Menu Button with specified icons for enabled and disabled states.
     *
     * @param icon
     *         icon for enabled state
     */
    public PopupItem(ImageResource icon) {
        this.icon = icon;

        panel = new ButtonPanel();
        initWidget(panel);
        panel.setStyleName(css.popupButtonPanel());
        if (icon != null) {
            Image image = new Image(icon);
            image.setStyleName(css.popupButtonIcon());
            panel.add(image);
        }
        renderIcon();
        InlineLabel caret = new InlineLabel("");
        caret.setStyleName(css.caret());
        panel.add(caret);
    }

    /**
     * Adds new item to the Popup Menu which will be displayed when this Popup Button was clicked.
     *
     * @param title
     *         title of new item
     * @return new instance of PopupMenuItem
     */
    public Item addItem(String title) {
        return addItem(null, title, null);
    }

    /**
     * Adds new item to the Popup Menu which will be displayed when this Popup Button was clicked.
     *
     * @param title
     *         title of new item
     * @param command
     *         command which will be executed when Popup Menu Item was pressed.
     * @return new instance of PopupMenuItem
     */
    public Item addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /**
     * Adds new item to the Popup Menu which will be displayed when this Popup Button was clicked.
     *
     * @param icon
     *         icon
     * @param title
     *         title of new item
     * @return new instance of PopupMenuItem
     */
    public Item addItem(ImageResource icon, String title) {
        return addItem(icon, title, null);
    }

    /**
     * Adds new item to the Popup Menu which will be displayed when this Popup Button was clicked.
     *
     * @param icon
     *         icon
     * @param title
     *         title of new item
     * @param command
     *         command which will be executed when Popup Menu Item was pressed.
     * @return new instance of PopupMenuItem
     */
    public Item addItem(ImageResource icon, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(icon, title, command);
        menuItems.add(item);
        return item;
    }

    /** Closes Popup Menu ( if opened ) and sets style of this Popup Menu Button to default. */
    protected void closePopupMenu() {
        if (popupMenu != null) {
            popupMenu.removeFromParent();
            popupMenu = null;
        }

        if (lockLayer != null) {
            lockLayer.removeFromParent();
            lockLayer = null;
        }

        panel.setStyleName(css.popupButtonPanel());
    }

    /**
     * Get icon which is used by this button for enabled state.
     *
     * @return icon for enabled state
     */
    public ImageResource getIcon() {
        return icon;
    }

    /**
     * Get list of Menu Items.
     *
     * @return list of menu items
     */
    public JsonArray<Item> getMenuItems() {
        return menuItems;
    }

    @Override
    public Command getCommand() {
        return null;
    }

    @Override
    public String getHotKey() {
        return null;
    }

    @Override
    public ImageResource getImage() {
        return null;
    }

    @Override
    public JsonArray<Item> getItems() {
        return null;
    }

    /**
     * Get is this button enabled.
     *
     * @return is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set is enabled.
     *
     * @param enabled
     *         is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        renderIcon();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

    /** {@inheritDoc} */
    public void onCloseMenu() {
        closePopupMenu();
    }

    /** {@inheritDoc} */
    public void onMenuItemSelected(Item Item) {
        closePopupMenu();
    }

    /** Mouse Down handler. */
    private void onMouseDown() {
        panel.setStyleName(css.popupButtonPanelDown());
    }

    /** Mouse Out Handler. */
    private void onMouseOut() {
        if (popupMenu != null) {
            return;
        }

        panel.setStyleName(css.popupButtonPanel());
    }

    private void onMouseClick() {
        openPopupMenu();
    }

    /** Mouse Over handler. */
    private void onMouseOver() {
        panel.setStyleName(css.popupButtonPanelOver());
    }

    /** Mouse Up handler. */
    private void onMouseUp() {
        panel.setStyleName(css.popupButtonPanelOver());
    }

    /** Opens Popup Menu. */
    public void openPopupMenu() {
        lockLayer = new MenuLockLayer(this);

        popupMenu = new PopupMenu(menuItems, lockLayer, this, "toolbar");
        lockLayer.add(popupMenu);

        int left = getAbsoluteLeft();
        int top = getAbsoluteTop() + 24;
        popupMenu.getElement().getStyle().setTop(top, com.google.gwt.dom.client.Style.Unit.PX);
        popupMenu.getElement().getStyle().setLeft(left, com.google.gwt.dom.client.Style.Unit.PX);
    }

    /** Redraw icon. */
    private void renderIcon() {
        if (enabled) {
            panel.getElement().removeClassName(css.disabled());
        } else {
            panel.getElement().addClassName(css.disabled());
        }

//        Element e = panel.getElement();
//        Element imageElement = DOM.getChild(e, 0);
//
//        //NOT WORK in IE!!!
//        //DOM.setElementAttribute(imageElement, "class", Style.POPUP_ICON);
//        imageElement.setClassName(css.popupButtonIcon());
    }

    /**
     * Sets the title associated with this button. The title is the 'tool-tip'
     * displayed to users when they hover over the object.
     *
     * @param title
     *         the object's new title
     */
    public void setTitle(String title) {
        panel.setTitle(title);
    }

    public void addItem(PopupRowItem menuItem) {
        menuItems.add(menuItem);
    }

    public static class PopupRowItem implements ToolbarItem {
        private final String        title;
        private final ImageResource image;
        private final Command       command;
        private final String        hotkey;
        private final String        tooltip;
        private       boolean       enebled;
        private       boolean       selected;
        private       boolean       visible;


        public PopupRowItem(String title, ImageResource image, Command command, String hotkey, String tooltip) {

            this.title = title;
            this.image = image;
            this.command = command;
            this.hotkey = hotkey;
            this.tooltip = tooltip;
        }

        /** {@inheritDoc} */
        @Override
        public Widget asWidget() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public Command getCommand() {
            return command;
        }

        /** {@inheritDoc} */
        @Override
        public String getHotKey() {
            return hotkey;
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
        public String getTitle() {
            return title;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isEnabled() {
            return this.enebled;
        }

        /** {@inheritDoc} */
        @Override
        public void setEnabled(boolean enabled) {
            this.enebled = enabled;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isSelected() {
            return this.selected;
        }

        /** {@inheritDoc} */
        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isVisible() {
            return this.visible;
        }

        /** {@inheritDoc} */
        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    /** This class uses to handling mouse events on Popup Button. */
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
                    if (event.getButton() == Event.BUTTON_LEFT) {
                        onMouseDown();
                    }
                    break;

                case Event.ONMOUSEUP:
                    if (event.getButton() == Event.BUTTON_LEFT) {
                        onMouseUp();
                    }
                    break;

                case Event.ONCLICK:
                    onMouseClick();
                    break;

            }
        }

    }
}
