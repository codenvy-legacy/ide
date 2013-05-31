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
package org.exoplatform.ide.client.project;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 2:12:50 PM anya $
 */
@RolesAllowed({"developer"})
public class ProjectPaaSControl extends SimpleControl implements IDEControl,
         ProjectOpenedHandler, ProjectClosedHandler, FolderRefreshedHandler,
         ItemsSelectedHandler
{

    public static final String ID = "Project/PaaS";

    private static final String TITLE = "PaaS";

    private static final String PROMPT = "PaaS";

    public ProjectPaaSControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.paas(), IDEImageBundle.INSTANCE.paasDisabled());
        setGroupName(GroupNames.PAAS);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(false);

        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(FolderRefreshedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.project
     * .ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
        setVisible(false);
    }

    /** @see org.exoplatform.ide.client.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.project
     * .ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        boolean enabled = isDeployed(event.getProject());
        setEnabled(enabled);
        setVisible(enabled);
    }

    /**
     * Check project is deployed to one of the PaaS.
     *
     * @param project
     *         project
     * @return {@link Boolean} <code>true</code> if deployed to one of the PaaS
     */
    private boolean isDeployed(ProjectModel project) {
        if (project == null) {
            return false;
        }

        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());

        return (project.getProperty("cloudbees-application") != null  &&  !project.getPropertyValues("cloudbees-application").isEmpty())
               || (project.getProperty("heroku-application") != null &&  !project.getPropertyValues("heroku-application").isEmpty())
               || (project.getProperty("openshift-express-application") != null  &&  !project.getPropertyValues("openshift-express-application").isEmpty())
               || (project.getProperty("cloudfoundry-application") != null &&  !project.getPropertyValues("cloudfoundry-application").isEmpty())
               || (project.getProperty("tier3webfabric-application") != null &&  !project.getPropertyValues("tier3webfabric-application").isEmpty())
               || (project.getProperty("appfog-application") != null &&  !project.getPropertyValues("appfog-application").isEmpty())
               || ProjectResolver.APP_ENGINE_JAVA.equals(project.getProjectType())
               || ProjectResolver.APP_ENGINE_PYTHON.equals(project.getProjectType())
               || (targets != null && targets.contains("GAE"));
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     * .framework.navigation.event.FolderRefreshedEvent) */
    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        if (event.getFolder() instanceof ProjectModel) {
            boolean enabled = isDeployed((ProjectModel)event.getFolder());
            setEnabled(enabled);
            setVisible(enabled);
        }
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            return;
        }

        Item selectedItem = event.getSelectedItems().get(0);
        ProjectModel project = selectedItem instanceof ProjectModel ? (ProjectModel)selectedItem :
                               ((ItemContext)selectedItem).getProject();
        boolean enabled = isDeployed(project);
        setEnabled(enabled);
        setVisible(enabled);
    }

}
