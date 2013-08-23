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
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed({"developer"})
public class SendCodePointerControl extends SimpleControl implements IDEControl, ViewActivatedHandler, ProjectOpenedHandler,
                                                                     CollaborationChangedHandler {

    private static final String ID = "Edit/Send Code Pointer";

    public SendCodePointerControl(ChatResources resources) {
        super(ID);
        setTitle("Send Code Pointer");
        setPrompt("Send Code Pointer");
        setEvent(new SendCodePointEvent());
        setEnabled(false);
        setVisible(true);
        setShowInContextMenu(true);
        setImages(resources.blank(), resources.blank());
        setShowInMenu(false);

    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        IDE.eventBus().addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(CollaborationChangedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        setShowInContextMenu(event.getView().getId().contains("editor-"));
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setVisible(CollaborationPropertiesUtil.isCollaborationEnabled(event.getProject()));
    }

    /** {@inheritDoc} */
    @Override
    public void onCollaborationChanged(CollaborationChangedEvent event) {
        setVisible(event.isEnabled());
    }
}
