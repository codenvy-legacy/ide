/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.menu;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PopupMenu;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.toolbar.Utils;
import com.google.gwt.dom.client.Element;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 *          <p/>
 *          Menu bar is implementation of Menu interface and represents a visual component.
 */
public class MenuBarItem implements ActionSelectedHandler {

    private final ActionGroup         group;
    private final ActionManager       actionManager;
    private final PresentationFactory presentationFactory;
    private final String              place;
    /**
     * Working variable:
     * is need to store hovered or normal state.
     */
    boolean hovered = false;
    /**
     * Working variable:
     * is need to store pressed state.
     */
    boolean pressed = false;
    /** Visual element which is table cell. */
    private Element element;
    /** Enabled or disabled state */
    private boolean enabled         = true;
    private boolean hasVisibleItems = true;
    private ActionSelectedHandler actionSelectedHandler;
    private KeyBindingAgent       keyBindingAgent;
    private MenuResources.Css     css;
    /**
     * Working variable:
     * is needs to store opened Popup menu.
     */
    private PopupMenu             popupMenu;

    /** Title of Menu Bar Item */
    private String title;

    public MenuBarItem(ActionGroup group, ActionManager actionManager, PresentationFactory presentationFactory, String place,
                       Element element, ActionSelectedHandler handler, KeyBindingAgent keyBindingAgent, MenuResources.Css css) {
        this.group = group;
        this.actionManager = actionManager;
        this.presentationFactory = presentationFactory;
        this.place = place;
        this.element = element;
        this.actionSelectedHandler = handler;
        this.keyBindingAgent = keyBindingAgent;
        this.css = css;
        Presentation presentation = presentationFactory.getPresentation(group);
        title = presentation.getText();
        element.setInnerText(presentation.getText());
        setEnabled(Utils.hasVisibleChildren(group, presentationFactory, actionManager, place));
    }

    /** Close opened Popup Menu. */
    public void closePopupMenu() {
        popupMenu.closePopup();
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateEnabledState();
    }

    /** Mouse Down handler */
    public boolean onMouseDown() {
        if (enabled && hasVisibleItems) {
            element.setClassName(css.menuBarItemSelected());
            pressed = true;
            actionSelectedHandler.onActionSelected(group);
            return true;
        }

        return false;
    }

    /** Mouse Out Handler */
    public void onMouseOut() {
        if (pressed) {
            return;
        }

        if (enabled && hasVisibleItems) {
            element.setClassName(css.menuBarItem());
        } else {
            element.setClassName(css.menuBarItemDisabled());
        }
    }

    /** Mouse Over Handler */
    public void onMouseOver() {
        if (pressed) {
            return;
        }

        if (enabled && hasVisibleItems) {
            element.setClassName(css.menuBarItemOver());
            hovered = true;
        }
    }

    /**
     * Open sub Popup Menu
     *
     * @param menuLockLayer
     *         - lock layer which will receive PopupMenu visual component and
     */
    public void openPopupMenu(MenuLockLayer menuLockLayer) {
        int x = element.getAbsoluteLeft();
        int y = 0;
        popupMenu =
                new PopupMenu(group, actionManager, place, presentationFactory, menuLockLayer, this, keyBindingAgent, "topmenu/" + title);
        menuLockLayer.add(popupMenu, x, y);
    }

    /** Reset visual state of Menu Bar Item to default. */
    public void setNormalState() {
        pressed = false;
        element.setClassName(css.menuBarItem());
    }

    private void updateEnabledState() {
        pressed = false;
        if (enabled && hasVisibleItems) {
            element.setClassName(css.menuBarItem());
        } else {
            element.setClassName(css.menuBarItemDisabled());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onActionSelected(Action action) {
        setNormalState();
        actionSelectedHandler.onActionSelected(action);
    }

    public String getTitle() {
        return title;
    }

    public void update() {
        setEnabled(Utils.hasVisibleChildren(group, presentationFactory, actionManager, place));
    }
}
