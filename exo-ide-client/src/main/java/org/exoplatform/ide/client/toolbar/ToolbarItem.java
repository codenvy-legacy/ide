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
package org.exoplatform.ide.client.toolbar;

import org.exoplatform.gwtframework.ui.client.command.Control;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarItem {

    public static enum Type {

        COMMAND, DELIMITER, SPACER

    }

    private Type type;

    private String id;

    private Control command;

    public ToolbarItem(Type type) {
        this.type = type;
    }

    public ToolbarItem(Type type, String id, Control command) {
        this.type = type;
        this.id = id;
        this.command = command;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Control getCommand() {
        return command;
    }

}
