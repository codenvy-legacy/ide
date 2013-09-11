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
package org.exoplatform.ide.client.hotkeys;

import org.exoplatform.gwtframework.ui.client.command.Control;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class HotKeyItem {

    private String hotKey;

    private Control command;

    // --- fields for group item
    private boolean isGroup = false;

    private String title;

    private String group;

    public HotKeyItem(Control command, String hotkey, String group) {
        this.command = command;
        this.hotKey = hotkey;
        this.group = group;
    }

    public HotKeyItem(String title, String hotkey, boolean isGroup, String group) {
        this.title = title;
        this.hotKey = hotkey;
        this.isGroup = isGroup;
        this.group = group;
    }

    /** @return the group */
    public String getGroup() {
        return group;
    }

    /** @return the isGroup */
    public boolean isGroup() {
        return isGroup;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    public String getHotKey() {
        return hotKey;
    }

    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    /** @return the command */
    public Control getCommand() {
        return command;
    }

}
