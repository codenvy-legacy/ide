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
package com.google.collide.client.disable;

import com.google.collide.client.Resources;
import com.google.collide.client.collaboration.CollaborationPropertiesUtil;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DisableEnableCollaborationControl extends SimpleControl implements IDEControl, ProjectOpenedHandler, ProjectClosedHandler {

    public static final  String ID            = "Project/Disable \\ Enable Collaboration";
    private static final String ENABLE_TITLE  = "Enable Collaboration Mode";
    private static final String DISABLE_TITLE = "Disable Collaboration Mode";

    private DisableEnableCollaborationEvent enableEvent = new DisableEnableCollaborationEvent(true, true);
    private DisableEnableCollaborationEvent disableEvent = new DisableEnableCollaborationEvent(false, true);

    public DisableEnableCollaborationControl(Resources resources) {
        super(ID);
        setTitle(DISABLE_TITLE);
        setPrompt(DISABLE_TITLE);
        setEnabled(false);
        setVisible(true);
        setEvent(disableEvent);
        setImages(resources.getCollaborationImage(), resources.getCollaborationImageDisabled());
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(true);
        boolean enabled = CollaborationPropertiesUtil.isCollaborationEnabled(event.getProject());
        setState(enabled);
        Set<String> permissions = event.getProject().getPermissions();
        if (permissions != null) {
            setVisible(permissions.contains("write") || permissions.contains("all"));
        }
    }

    public void setState(boolean collaborationEnabled){
        if(collaborationEnabled){
            setEvent(disableEvent);
            setTitle(DISABLE_TITLE);
        }else{
            setEvent(enableEvent);
            setTitle(ENABLE_TITLE);
        }

    }
}
