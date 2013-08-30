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
package org.exoplatform.gwtframework.ui.client.command;

import com.google.gwt.event.shared.GwtEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SimpleControl extends Control<SimpleControl> {

    private String title;

    private boolean showInMenu;

    private boolean showInContextMenu;

    private boolean canBeSelected;

    private boolean selected;

    private String hotKey;

    /**
     * If true, then this command will call by hotkey,
     * even if it will be disabled.
     */
    private boolean ignoreDisable;

    private GwtEvent<?> event;

    private String groupName;

    //this attributes appends to popup menu item to allow append to item events e.g. onClick or other things
    private Map<String, String> attributes;

    public SimpleControl(String id) {
        super(id);
    }

   /*
    * TITLE
    */

    public String getTitle() {
        return title;
    }

    public SimpleControl setTitle(String title) {
        if (this.title == title) {
            return this;
        }

        this.title = title;
        for (ControlStateListener listener : getStateListeners()) {
            if (!(listener instanceof SimpleControlStateListener)) {
                continue;
            }

            ((SimpleControlStateListener)listener).updateControlTitle(title);
        }

        return this;
    }

   /*
    * SHOW IN MENU
    */

    public boolean isShowInMenu() {
        return showInMenu;
    }

    public SimpleControl setShowInMenu(boolean showInMenu) {
        this.showInMenu = showInMenu;
        return this;
    }

    /*
     * SHOW IN CONTEXT MENU
     */
    public boolean isShowInContextMenu() {
        return showInContextMenu;
    }

    public SimpleControl setShowInContextMenu(boolean showInContextMenu) {
        this.showInContextMenu = showInContextMenu;
        return this;
    }

   /*
    * CALL BY HOTKEY AND IGNORE DISABLE
    */

    public boolean isIgnoreDisable() {
        return ignoreDisable;
    }

    public SimpleControl setIgnoreDisable(boolean ignoreDisable) {
        this.ignoreDisable = ignoreDisable;
        return this;
    }

   /*
    * EVENT
    */

    public GwtEvent<?> getEvent() {
        return event;
    }

    public SimpleControl setEvent(GwtEvent<?> event) {
        this.event = event;
        return this;
    }

    public String getHotKey() {
        return hotKey;
    }

    public SimpleControl setHotKey(String hotKey) {
        this.hotKey = hotKey;

        for (ControlStateListener listener : getStateListeners()) {
            if (!(listener instanceof SimpleControlStateListener)) {
                continue;
            }

            ((SimpleControlStateListener)listener).updateControlHotKey(hotKey);
        }

        return this;
    }

    public boolean canBeSelected() {
        return canBeSelected;
    }

    public SimpleControl setCanBeSelected(boolean canBeSelected) {
        this.canBeSelected = canBeSelected;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public SimpleControl setSelected(boolean selected) {
        if (this.selected == selected) {
            return this;
        }

        this.selected = selected;

        for (ControlStateListener listener : getStateListeners()) {
            if (!(listener instanceof SimpleControlStateListener)) {
                continue;
            }

            ((SimpleControlStateListener)listener).updateControlSelectionState(selected);
        }

        return this;
    }

    /**
     * Returns group name
     *
     * @return group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets new group name
     *
     * @param groupName
     *         new group name
     */
    public SimpleControl setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Map<String, String> getAttributes() {
        if (attributes == null)
            attributes = new HashMap<String, String>();
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
