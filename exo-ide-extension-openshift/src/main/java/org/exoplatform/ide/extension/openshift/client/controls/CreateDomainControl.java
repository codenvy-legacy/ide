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
package org.exoplatform.ide.extension.openshift.client.controls;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedEvent;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.domain.CreateDomainEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control is used for new domain creation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 2:22:44 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class CreateDomainControl extends AbstractOpenShiftControl implements ProjectClosedHandler, ProjectOpenedHandler,
                                                                 PropertiesChangedHandler {

    public CreateDomainControl() {
        super(OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainControlId());
        setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainControlTitle());
        setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainControlPrompt());
        setImages(OpenShiftClientBundle.INSTANCE.createDomainControl(),
                  OpenShiftClientBundle.INSTANCE.createDomainControlDisabled());
        setEvent(new CreateDomainEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(PropertiesChangedEvent.TYPE, this);

        setVisible(true);
    }

    @Override
    public void onPropertiesChanged(PropertiesChangedEvent event) {
        ProjectModel project = event.getProject();
        while (project.getProject() != null) {
            project = project.getProject();
        }
        setEnabled(OpenShiftExtension.canBeDeployedToOpenShift(project));
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        ProjectModel project = event.getProject();
        while (project.getProject() != null) {
            project = project.getProject();
        }
        setEnabled(OpenShiftExtension.canBeDeployedToOpenShift(project));
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);

    }
}
