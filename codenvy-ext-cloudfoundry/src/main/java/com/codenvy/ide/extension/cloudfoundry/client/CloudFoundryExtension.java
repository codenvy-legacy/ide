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

import com.codenvy.ide.api.ui.menu.MainMenuAgent;
import com.codenvy.ide.api.ui.paas.PaaSAgent;
import com.codenvy.ide.extension.Extension;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowApplicationsCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowCloudFoundryProjectCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowCreateApplicationCommand;
import com.codenvy.ide.extension.cloudfoundry.client.command.ShowLoginCommand;
import com.codenvy.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.loader.EmptyLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
    * @param showCloudFoundryProjectCommand
    * @param eventBus
    * @param deployAppPresenter
    * @param constant
    * @param autoBeanFactory
    */
   @Inject
   public CloudFoundryExtension(PaaSAgent paasAgent, CloudFoundryResources resources, MainMenuAgent menu,
      ShowCreateApplicationCommand createApplicationCommand, ShowLoginCommand loginCommand,
      ShowApplicationsCommand showApplicationsCommand, ShowCloudFoundryProjectCommand showCloudFoundryProjectCommand,
      EventBus eventBus, DeployApplicationPresenter deployAppPresenter, CloudFoundryLocalizationConstant constant,
      CloudFoundryAutoBeanFactory autoBeanFactory)
   {
      resources.cloudFoundryCss().ensureInjected();

      // TODO change hard code types
      JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "Rails", "Spring", "War");
      paasAgent.registerPaaS(ID, ID, resources.cloudFoundry48(), false, requiredProjectTypes, deployAppPresenter, null);
     
      // TODO Need get service from DI?
      String restContext = "/rest/private";
      new CloudFoundryClientServiceImpl(restContext, new EmptyLoader(), null, eventBus, constant, autoBeanFactory);

      menu.addMenuItem("PaaS/CloudFoudry/Create Application...", createApplicationCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Applications...", showApplicationsCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Switch Account...", loginCommand);
      menu.addMenuItem("Project/Paas/CloudFoudry", showCloudFoundryProjectCommand);
   }
}