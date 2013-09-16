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
package org.eclipse.jdt.client.packaging;

import org.eclipse.jdt.client.JdtClientBundle;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ShowPackageExplorerControl extends SimpleControl implements IDEControl, VfsChangedHandler,
                                                                         ViewOpenedHandler, ViewClosedHandler, ProjectClosedHandler,
                                                                         ProjectOpenedHandler {

    public static final String ID = "Window/Show View/Package Explorer";

    private static final String TITLE = "Package Explorer";

    private static final String PROMPT = "Package Explorer";

    private ProjectModel project;

    public ShowPackageExplorerControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(JdtClientBundle.INSTANCE.packageExplorer(), JdtClientBundle.INSTANCE.packageExplorerDisabled());
        setEvent(new ShowPackageExplorerEvent());
        setEnabled(false);
    }

    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (event.getVfsInfo() == null) {
            setEnabled(false);
            setVisible(false);
        } else {
            setVisible(true);
        }

        setEnabled(JavaProjects.contains(project));
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof PackageExplorerDisplay) {
            setSelected(false);
        }
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof PackageExplorerDisplay) {
            setSelected(true);
        }
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        setEnabled(JavaProjects.contains(project));
    }

}
