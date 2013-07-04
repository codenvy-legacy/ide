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
