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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter;
import org.exoplatform.ide.extension.cloudbees.client.control.ApplicationListControl;
import org.exoplatform.ide.extension.cloudbees.client.control.CloudBeesControl;
import org.exoplatform.ide.extension.cloudbees.client.control.InitializeApplicationControl;
import org.exoplatform.ide.extension.cloudbees.client.delete.DeleteApplicationPresenter;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationPresenter;
import org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginPresenter;
import org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter;
import org.exoplatform.ide.extension.cloudbees.client.update.UpdateApplicationPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;
import java.util.List;

/**
 * CloudBees extension for IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesExtension.java Jun 23, 2011 10:11:59 AM vereshchaka $
 */
public class CloudBeesExtension extends Extension implements InitializeServicesHandler {

    /** The generator of an {@link AutoBean}. */
    public static final CloudBeesAutoBeanFactory      AUTO_BEAN_FACTORY     = GWT.create(CloudBeesAutoBeanFactory.class);

    public static final CloudBeesLocalizationConstant LOCALIZATION_CONSTANT = GWT
                                                                                 .create(CloudBeesLocalizationConstant.class);

    public static final String                        ID                    = "CloudBees";

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new CloudBeesClientServiceImpl(event.getLoader(), IDE.messageBus());
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(
                                       new PaaS("CloudBees", "CloudBees", new Image(CloudBeesClientBundle.INSTANCE.cloudBees48()),
                                                new Image(
                                                          CloudBeesClientBundle.INSTANCE.cloudBees48Disabled()),
                                                Arrays.asList(ProjectType.JSP, ProjectType.WAR),
                                                new org.exoplatform.ide.extension.cloudbees.client.deploy.DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new CloudBeesControl());
        IDE.getInstance().addControl(new InitializeApplicationControl());
        IDE.getInstance().addControl(new ApplicationListControl());

        new InitializeApplicationPresenter();
        new LoginPresenter();
        new ApplicationInfoPresenter();
        new DeleteApplicationPresenter();
        new ApplicationListPresenter();
        new UpdateApplicationPresenter();
        new CloudBeesProjectPresenter();
        new CreateAccountPresenter();
    }

    public static boolean canBeDeployedToCB(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());
        return (targets != null && targets.contains(ID));
    }
}
