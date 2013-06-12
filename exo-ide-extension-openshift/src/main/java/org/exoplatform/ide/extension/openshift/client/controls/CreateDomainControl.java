/*
 * Copyright (C) 2011 eXo Platform SAS.
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
@RolesAllowed("developer")
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
