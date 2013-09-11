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
package com.codenvy.ide.factory.client.copy;

import com.codenvy.ide.factory.client.FactoryClientBundle;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CopyProjectControl extends SimpleControl implements IDEControl, ProjectOpenedHandler {

    public CopyProjectControl() {
        super("Project/Copy");
        setTitle("Copy project");
        setPrompt("Copy project");
        setEnabled(false);
        setVisible(true);
        setImages(FactoryClientBundle.INSTANCE.share(), FactoryClientBundle.INSTANCE.shareDisabled());
        setEvent(new CopyProjectEvent());
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if(IDE.currentWorkspace.isTemporary() && !IDE.user.isTemporary() && userHasPermanentWs()){
            setEnabled(true);
        }
    }


    private boolean userHasPermanentWs(){
        List<WorkspaceInfo> workspaces = IDE.user.getWorkspaces();
        if (workspaces == null || workspaces.isEmpty())
            return false;
        for (WorkspaceInfo workspace : workspaces) {
            if (!workspace.isTemporary())
                return true;
        }
        return false;
    }

}
