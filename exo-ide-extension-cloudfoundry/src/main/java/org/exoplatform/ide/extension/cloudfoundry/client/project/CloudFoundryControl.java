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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * Control for managing project, deployed on CloudFoundry.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 2, 2011 5:39:01 PM anya $
 */
@RolesAllowed("developer")
public class CloudFoundryControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
                                                      ProjectClosedHandler, FolderRefreshedHandler {
    private static final String CF_ID     = "Project/PaaS/CloudFoundry";

    private static final String WF_ID     = "Project/PaaS/Tier3 Web Fabric";

    private static final String CF_TITLE  = CloudFoundryExtension.LOCALIZATION_CONSTANT.cloudFoundryControlTitle();

    private static final String WF_TITLE  = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricControlTitle();

    private static final String CF_PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.cloudFoundryControlPrompt();

    private static final String WF_PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricControlPrompt();

    private final PAAS_PROVIDER paasProvider;

    public CloudFoundryControl(PAAS_PROVIDER paasProvider) {
        super(paasProvider == WEB_FABRIC ? WF_ID : CF_ID);
        this.paasProvider = paasProvider;
        setTitle(paasProvider == WEB_FABRIC ? WF_TITLE : CF_TITLE);
        setPrompt(paasProvider == WEB_FABRIC ? WF_PROMPT : CF_PROMPT);
        if (paasProvider == WEB_FABRIC) {
            setImages(CloudFoundryClientBundle.INSTANCE.tier3WebFabric16(),
                      CloudFoundryClientBundle.INSTANCE.tier3WebFabric16Disabled());
        } else {
            setImages(CloudFoundryClientBundle.INSTANCE.cloudFoundry(),
                      CloudFoundryClientBundle.INSTANCE.cloudFoundryDisabled());
        }
        setEvent(new ManageCloudFoundryProjectEvent(paasProvider));
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(FolderRefreshedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     *      .project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     *      .project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        update(event.getProject());
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     *      .framework.navigation.event.FolderRefreshedEvent)
     */
    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        if (event.getFolder() instanceof ProjectModel) {
            update((ProjectModel)event.getFolder());
        }
    }

    private void update(ProjectModel project) {
        boolean isCloudFoundry = paasProvider == CLOUD_FOUNDRY && project.getPropertyValue("cloudfoundry-application") != null;
        boolean isWebFabric = paasProvider == WEB_FABRIC && project.getPropertyValue("tier3webfabric-application") != null;
        setVisible(isCloudFoundry || isWebFabric);
        setEnabled(isCloudFoundry || isWebFabric);
    }
}
