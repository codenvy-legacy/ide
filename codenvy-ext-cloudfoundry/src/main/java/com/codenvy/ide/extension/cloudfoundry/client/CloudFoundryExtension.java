/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.api.ui.paas.PaaSAgent;
import com.codenvy.ide.extension.Extension;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowApplicationsCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowCreateApplicationCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowLoginCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowProjectPropertiesCommand;
import com.codenvy.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.loader.EmptyLoader;
import com.codenvy.ide.menu.MainMenuPresenter;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Extension add Cloud Foundry support to the IDE Application.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Cloud Foundry Support.", version = "3.0.0")
public class CloudFoundryExtension
{
   /**
    * The generator of an {@link AutoBean}.
    */
   public static final CloudFoundryAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(CloudFoundryAutoBeanFactory.class);

   /**
    * Default CloudFoundry server.
    */
   public static final String DEFAULT_SERVER = "http://api.cloudfoundry.com";

   public static final String ID = "CloudFoundry";

   /**
    * Create CloudFoundry extension.
    * 
    * @param paasAgent
    * @param resources
    * @param menu
    * @param createApplicationCommand
    * @param loginCommand
    * @param showApplicationsCommand
    * @param showProjectPropertiesCommand
    * @param eventBus
    * @param deployAppPresenter
    */
   @Inject
   public CloudFoundryExtension(PaaSAgent paasAgent, CloudFoundryResources resources, MainMenuPresenter menu,
      ShowCreateApplicationCommand createApplicationCommand, ShowLoginCommand loginCommand,
      ShowApplicationsCommand showApplicationsCommand, ShowProjectPropertiesCommand showProjectPropertiesCommand,
      EventBus eventBus, DeployApplicationPresenter deployAppPresenter, CloudFoundryLocalizationConstant constant)
   {
      resources.cloudFoundryCss().ensureInjected();

      // TODO change hard code types
      JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "Rails", "Spring", "War");
      paasAgent.registerPaaS(ID, ID, resources.cloudFoundry48(), false, requiredProjectTypes, deployAppPresenter, null);
     
      String restContext = "/rest/private";
      new CloudFoundryClientServiceImpl(restContext, new EmptyLoader(), null, eventBus, constant);

      menu.addMenuItem("PaaS/CloudFoudry/Create Application...", createApplicationCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Applications...", showApplicationsCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Switch Account...", loginCommand);
      menu.addMenuItem("Project/Paas/CloudFoudry", showProjectPropertiesCommand);
   }
}