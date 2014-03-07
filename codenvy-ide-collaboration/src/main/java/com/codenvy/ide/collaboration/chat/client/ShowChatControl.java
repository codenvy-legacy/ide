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
@RolesAllowed({"workspace/developer"})
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
