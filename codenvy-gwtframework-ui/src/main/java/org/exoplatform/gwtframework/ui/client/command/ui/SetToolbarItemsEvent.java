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

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.ui.client.command.Control;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SetToolbarItemsEvent extends GwtEvent<SetToolbarItemsHandler> {

    public static final GwtEvent.Type<SetToolbarItemsHandler> TYPE = new GwtEvent.Type<SetToolbarItemsHandler>();

    private List<Control> commands;

    private String toolbarId;

    private List<String> toolBarItems;

    public SetToolbarItemsEvent(List<String> toolBarItems, List<Control> commands) {
        this(null, toolBarItems, commands);
    }

    public SetToolbarItemsEvent(String toolbarId, List<String> toolBarItems, List<Control> commands) {
        this.toolbarId = toolbarId;
        this.toolBarItems = toolBarItems;
        this.commands = commands;
    }

    @Override
    protected void dispatch(SetToolbarItemsHandler handler) {
        handler.onSetToolbarItems(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SetToolbarItemsHandler> getAssociatedType() {
        return TYPE;
    }

    public List<Control> getCommands() {
        return commands;
    }

    public String getToolbarId() {
        return toolbarId;
    }

    public List<String> getToolBarItems() {
        return toolBarItems;
    }

}
