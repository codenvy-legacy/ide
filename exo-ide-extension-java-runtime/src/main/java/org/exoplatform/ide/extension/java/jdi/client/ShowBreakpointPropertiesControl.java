/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.java.jdi.client.events.*;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Control for show breakpoint properties.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowBreakpointPropertiesControl.java May 11, 2012 12:44:25 PM azatsarynnyy $
 */
@RolesAllowed({"developer"})
public class ShowBreakpointPropertiesControl extends SimpleControl implements IDEControl, ShowContextMenuHandler,
                                                                  DebuggerConnectedHandler, DebuggerDisconnectedHandler,
                                                                  ItemsSelectedHandler {
    /** Control's identifier. */
    public static final String  ID     = DebuggerExtension.LOCALIZATION_CONSTANT.showBreakpointPropertiesControlId();

    /** Control's title. */
    private static final String TITLE  = DebuggerExtension.LOCALIZATION_CONSTANT.showBreakpointPropertiesControlTitle();

    /** Control's prompt. */
    private static final String PROMPT = DebuggerExtension.LOCALIZATION_CONSTANT.showBreakpointPropertiesControlPrompt();

    public ShowBreakpointPropertiesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(DebuggerClientBundle.INSTANCE.breakpointProperties(),
                  DebuggerClientBundle.INSTANCE.breakpointPropertiesDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ShowContextMenuEvent.TYPE, this);
        IDE.addHandler(DebuggerConnectedEvent.TYPE, this);
        IDE.addHandler(DebuggerDisconnectedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);

        setVisible(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler#onShowContextMenu(org.exoplatform.ide.client
     *      .framework.contextmenu.ShowContextMenuEvent)
     */
    @Override
    public void onShowContextMenu(ShowContextMenuEvent event) {
        if (event.getObject() instanceof EditorBreakPoint) {
            setShowInContextMenu(true);
            setEvent(new ShowBreakpointPropertiesEvent(((EditorBreakPoint)event.getObject()).getBreakPoint()));
        } else if (event.getObject() instanceof BreakPoint) {
            setShowInContextMenu(true);
            setEvent(new ShowBreakpointPropertiesEvent((BreakPoint)event.getObject()));
        } else {
            setShowInContextMenu(false);
        }
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler#onDebuggerDisconnected(org.exoplatform.ide
     *      .extension.java.jdi.client.events.DebuggerDisconnectedEvent)
     */
    @Override
    public void onDebuggerDisconnected(DebuggerDisconnectedEvent event) {
        setVisible(false);
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler#onDebuggerConnected(org.exoplatform.ide
     *      .extension.java.jdi.client.events.DebuggerConnectedEvent)
     */
    @Override
    public void onDebuggerConnected(DebuggerConnectedEvent event) {
        setVisible(true);
        setEnabled(true);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            setVisible(false);
        } else {
            setVisible(true);
            Item selectedItem = event.getSelectedItems().get(0);

            ProjectModel project = selectedItem instanceof ProjectModel ? (ProjectModel)selectedItem
                : ((ItemContext)selectedItem).getProject();
            if (ProjectType.MultiModule.value().equals(project.getProjectType())
                || ProjectType.JAR.value().equals(project.getProjectType())
                || ProjectType.JAVASCRIPT.value().equals(project.getProjectType())
                || ProjectType.RUBY_ON_RAILS.value().equals(project.getProjectType())
                || ProjectType.PHP.value().equals(project.getProjectType())
                || ProjectType.PYTHON.value().equals(project.getProjectType())
                || ProjectType.NODE_JS.value().equals(project.getProjectType())) {
                setVisible(false);
            }
        }
    }

}
