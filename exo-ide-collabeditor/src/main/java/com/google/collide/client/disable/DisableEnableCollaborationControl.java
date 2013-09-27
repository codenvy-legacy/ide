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
