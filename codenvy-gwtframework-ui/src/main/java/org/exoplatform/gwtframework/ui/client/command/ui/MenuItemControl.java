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
import com.google.gwt.user.client.Command;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuItemControl implements Command, SimpleControlStateListener {

    private HandlerManager eventBus;

    private MenuItem menuItem;

    private SimpleControl command;

    public MenuItemControl(HandlerManager eventBus, MenuItem menuItem, SimpleControl command) {
        this.eventBus = eventBus;
        this.menuItem = menuItem;
        this.command = command;

        updateItemIcon();
        menuItem.setTitle(command.getTitle());
        menuItem.setCommand(this);
        menuItem.setEnabled(command.isEnabled());
        menuItem.setVisible(command.isVisible());
        menuItem.setSelected(command.isSelected());
        menuItem.setHotKey(command.getHotKey());
        command.getStateListeners().add(this);
    }

    private void updateItemIcon() {
        String icon = "";
        if (command.isEnabled()) {
            if (command.getNormalImage() != null) {
                icon = ImageHelper.getImageHTML(command.getNormalImage());
            } else if (command.getIcon() != null) {
                icon = ImageHelper.getImageHTML(command.getIcon());
            }
        } else {
            if (command.getDisabledImage() != null) {
                icon = ImageHelper.getImageHTML(command.getDisabledImage());
            } else if (command.getIcon() != null) {
                String iconNormal = command.getIcon();
                String iconDisabled = iconNormal.substring(0, iconNormal.lastIndexOf("."));
                iconDisabled += "_Disabled";
                iconDisabled += iconNormal.substring(iconNormal.lastIndexOf("."));
                icon = ImageHelper.getImageHTML(iconDisabled);
            }
        }

        menuItem.setIcon(icon);
    }

    public void updateControlEnabling(boolean enabled) {
        menuItem.setEnabled(enabled);
        updateItemIcon();
    }

    public void updateControlVisibility(boolean visible) {
        menuItem.setVisible(visible);
    }

    public void updateControlPrompt(String prompt) {
    }

    public void updateControlIcon(String icon) {
        updateItemIcon();
    }

    public void execute() {
        if (command.getEvent() != null) {
            try {
                eventBus.fireEvent(command.getEvent());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void updateControlTitle(String title) {
        menuItem.setTitle(title);
    }

    public void updateControlSelectionState(boolean selected) {
        menuItem.setSelected(selected);
    }

    public void updateControlHotKey(String hotKey) {
        menuItem.setHotKey(hotKey);
    }

}

