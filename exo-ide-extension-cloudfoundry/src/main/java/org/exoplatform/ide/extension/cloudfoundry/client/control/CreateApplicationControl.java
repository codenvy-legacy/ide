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
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedEvent;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.canBeDeployedToCF;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.canBeDeployedToWF;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * Control for creating application on CloudFoundry/Tier 3 Web Fabric.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateApplicationControl.java Jul 7, 2011 5:32:27 PM vereshchaka $
 */
@RolesAllowed({"workspace/developer"})
public class CreateApplicationControl extends AbstractCloudFoundryControl implements ProjectOpenedHandler,
                                                                         ProjectClosedHandler, PropertiesChangedHandler {

    private static final String CF_ID  = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlId();

    private static final String WF_ID  = CloudFoundryExtension.LOCALIZATION_CONSTANT.createTier3WebFabricAppControlId();

    private static final String TITLE  = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlTitle();

    private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.createAppControlPrompt();

    private final PAAS_PROVIDER paasProvider;

    public CreateApplicationControl(PAAS_PROVIDER paasProvider) {
        super(paasProvider == WEB_FABRIC ? WF_ID : CF_ID);
        this.paasProvider = paasProvider;
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudFoundryClientBundle.INSTANCE.createApp(), CloudFoundryClientBundle.INSTANCE.createAppDisabled());
        setEvent(new CreateApplicationEvent(paasProvider));
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.control.AbstractCloudFoundryControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(PropertiesChangedEvent.TYPE, this);
        setVisible(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     *      .project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     *      .project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(event.getProject() != null
                   && ((paasProvider == CLOUD_FOUNDRY && canBeDeployedToCF(event.getProject())) || (paasProvider == WEB_FABRIC && canBeDeployedToWF(event.getProject()))));
    }

    @Override
    public void onPropertiesChanged(PropertiesChangedEvent event) {
        ProjectModel project = event.getProject();
        while (project.getProject() != null) {
            project = project.getProject();
        }
        setEnabled((paasProvider == CLOUD_FOUNDRY && canBeDeployedToCF(project))
                   || (paasProvider == WEB_FABRIC && canBeDeployedToWF(project)));
    }
}
