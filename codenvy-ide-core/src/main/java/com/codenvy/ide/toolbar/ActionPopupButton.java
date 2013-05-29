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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.ui.menu.CloseMenuHandler;
import com.codenvy.ide.ui.menu.MenuLockLayer;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ActionPopupButton extends Composite implements CloseMenuHandler, ActionSelectedHandler {

    private static final ToolbarResources.Css css = Toolbar.RESOURCES.toolbar();
    private final ActionGroup         action;
    private final ActionManager       actionManager;
    private       PresentationFactory presentationFactory;
    private final String              place;

    /** Enabled state. True as default. */
    private boolean enabled = true;
    /** Lock Layer uses for locking rest of the screen, which does not covered by Popup Menu. */
    private MenuLockLayer lockLayer;
    /** Popup Menu button panel (<div> HTML element). */
    private ButtonPanel   panel;
    /** Has instance if Popup Menu is opened. */
    private PopupMenu     popupMenu;

    /** Create Popup Menu Button with specified icons for enabled and disabled states. */
    public ActionPopupButton(final ActionGroup action, ActionManager actionManager, final PresentationFactory presentationFactory,
                             String place) {
        this.action = action;
        this.actionManager = actionManager;
        this.presentationFactory = presentationFactory;
        this.place = place;

        panel = new ButtonPanel();
        initWidget(panel);
        panel.setStyleName(css.popupButtonPanel());
        ImageResource icon = presentationFactory.getPresentation(action).getIcon();
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

    /** {@inheritDoc} */
    public void onCloseMenu() {
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

        popupMenu = new PopupMenu(action, actionManager, place, presentationFactory, lockLayer, this, "toolbar");
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
    }

    /** {@inheritDoc} */
    @Override
    public void onActionSelected(Action action) {
        closePopupMenu();
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
