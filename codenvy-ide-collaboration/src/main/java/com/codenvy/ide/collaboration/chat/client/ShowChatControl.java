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
package com.codenvy.ide.collaboration.chat.client;

import com.google.collide.client.collaboration.CollaborationPropertiesUtil;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.event.CollaborationChangedEvent;
import org.exoplatform.ide.client.framework.event.CollaborationChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed({"developer"})
public class ShowChatControl extends SimpleControl implements IDEControl, ProjectOpenedHandler, ProjectClosedHandler,
                                                              CollaborationChangedHandler {
    public static final String ID = "View/Collaboration";

    private ChatResources resources;


    public ShowChatControl(ChatResources resources) {
        super(ID);
        this.resources = resources;
        setTitle("Collaboration");
        setPrompt("Collaboration");
        setImages(resources.collaborators(), resources.collaboratorsDisabled());
        setEvent(new ShowHideChatEvent(true));
        setCanBeSelected(true);
        setVisible(true);
        setEnabled(false);
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(CollaborationChangedEvent.TYPE, this);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if(CollaborationPropertiesUtil.isCollaborationEnabled(event.getProject())){
          setEnabled(true);
        }
    }

    public void chatOpened(boolean opened) {
        setEvent(new ShowHideChatEvent(!opened));
        setSelected(opened);
    }

    public void startBlink() {
        setImages(resources.collaboratorsAnimation(), resources.collaboratorsDisabled());
    }

    public void stopBlink() {
        setImages(resources.collaborators(), resources.collaboratorsDisabled());
    }

    /** {@inheritDoc} */
    @Override
    public void onCollaborationChanged(CollaborationChangedEvent event) {
        setEnabled(event.isEnabled());
    }
}
