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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 9:57:22 AM Mar 5, 2012 evgen $
 */
@RolesAllowed({"developer"})
public class CleanProjectControl extends SimpleControl implements IDEControl, 
            ProjectOpenedHandler, ProjectClosedHandler {

    private boolean isJavaProject = false;

    public CleanProjectControl() {
        super(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlId());
        setTitle(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlTitle());
        setPrompt(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlPrompt());
        setImages(JdtClientBundle.INSTANCE.clean(), JdtClientBundle.INSTANCE.cleanDisabled());
        setEvent(new CleanProjectEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        setVisible(false);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        isJavaProject = false;
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        isJavaProject = JdtExtension.get().isProjectSupported(event.getProject().getProjectType());
        updateEnabling();
    }

    /**
     *
     */
    private void updateEnabling() {
        setVisible(isJavaProject);
        setEnabled(isJavaProject);
    }

}
