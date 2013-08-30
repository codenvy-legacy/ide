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

package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.ControlStateListener;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.component.PopupMenuButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupMenuButtonControl extends PopupMenuButton implements ControlStateListener {

    private final static String DISABLED_SUFFIX = "_Disabled";

    private HandlerManager eventBus;

    private PopupMenuControl popupMenuControl;

    private Toolbar toolbar;

    public PopupMenuButtonControl(HandlerManager eventBus, PopupMenuControl popupMenuControl, Toolbar toolbar) {
        super(null, null);

        this.eventBus = eventBus;
        this.popupMenuControl = popupMenuControl;
        this.toolbar = toolbar;

        setIcon(getControlIcon());
        setDisabledIcon(getControlDisabledIcon());
        setEnabled(popupMenuControl.isEnabled());
        setTitle(popupMenuControl.getPrompt());

        refreshPopupMenuItems();
    }

    protected String getControlIcon() {
        String icon = "";
        if (popupMenuControl.getNormalImage() != null) {
            icon = ImageHelper.getImageHTML(popupMenuControl.getNormalImage());
        } else if (popupMenuControl.getIcon() != null) {
            icon = ImageHelper.getImageHTML(popupMenuControl.getIcon());
        }

        return icon;
    }

    protected String getControlDisabledIcon() {
        String disabledIcon = "";
        if (popupMenuControl.getDisabledImage() != null) {
            disabledIcon = ImageHelper.getImageHTML(popupMenuControl.getDisabledImage());
        } else if (popupMenuControl.getIcon() != null) {
            String disabledIconURL = popupMenuControl.getIcon();
            disabledIconURL = popupMenuControl.getIcon().substring(0, popupMenuControl.getIcon().lastIndexOf("."));
            disabledIconURL += DISABLED_SUFFIX;
            disabledIconURL += popupMenuControl.getIcon().substring(popupMenuControl.getIcon().lastIndexOf("."));
            disabledIcon = ImageHelper.getImageHTML(disabledIconURL);
        }

        return disabledIcon;
    }

    public void refreshPopupMenuItems() {
        getMenuItems().clear();

        for (SimpleControl command : popupMenuControl.getCommands()) {
            String icon = getMenuItemIcon(command);

            if (command.hasDelimiterBefore()) {
                addItem(null);
            }

            MenuItem menuItem = addItem(icon, command.getTitle());
            new MenuItemControl(eventBus, menuItem, command);
        }
    }

    protected String getMenuItemIcon(SimpleControl command) {
        String icon;

        if (command.isEnabled()) {
            if (command.getNormalImage() != null) {
                icon = ImageHelper.getImageHTML(command.getNormalImage());
            } else {
                icon = ImageHelper.getImageHTML(command.getIcon());
            }
        } else {
            if (command.getDisabledImage() != null) {
                icon = ImageHelper.getImageHTML(command.getDisabledImage());
            } else {
                icon = ImageHelper.getImageHTML(command.getIcon());
            }

        }

        return icon;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getParent().setVisible(popupMenuControl.isVisible());
        popupMenuControl.getStateListeners().add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        popupMenuControl.getStateListeners().remove(this);
    }

    public void updateControlEnabling(boolean enabled) {
        setEnabled(enabled);
    }

    public void updateControlVisibility(boolean visible) {
        getParent().setVisible(visible);
        toolbar.hideDuplicatedDelimiters();
    }

    public void updateControlPrompt(String prompt) {
        setTitle(prompt);
    }

    public void updateControlIcon(String icon) {
    }

}
