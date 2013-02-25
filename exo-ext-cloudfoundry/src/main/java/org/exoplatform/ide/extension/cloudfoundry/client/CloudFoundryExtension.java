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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.ui.paas.PaaSAgent;
import org.exoplatform.ide.extension.Extension;
import org.exoplatform.ide.extension.cloudfoundry.client.command.ShowApplicationsCommand;
import org.exoplatform.ide.extension.cloudfoundry.client.command.ShowCreateApplicationCommand;
import org.exoplatform.ide.extension.cloudfoundry.client.command.ShowLoginCommand;
import org.exoplatform.ide.extension.cloudfoundry.client.command.ShowProjectPropertiesCommand;
import org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.loader.EmptyLoader;
import org.exoplatform.ide.menu.MainMenuPresenter;

/**
 * Extension add Cloud Foundry support to the IDE Application.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Cloud Foundry Support.", id = "ide.ext.cloudfoundry", version = "2.0.0")
public class CloudFoundryExtension
{
   /**
    * The generator of an {@link AutoBean}.
    */
   public static final CloudFoundryAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(CloudFoundryAutoBeanFactory.class);

   public static final CloudFoundryLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(CloudFoundryLocalizationConstant.class);

   /**
    * Default CloudFoundry server.
    */
   public static final String DEFAULT_SERVER = "http://api.cloudfoundry.com";

   public static final String ID = "CloudFoundry";

   @Inject
   public CloudFoundryExtension(PaaSAgent paasAgent, CloudFoundryResources resources, MainMenuPresenter menu,
      ShowCreateApplicationCommand createApplicationCommand, ShowLoginCommand loginCommand,
      ShowApplicationsCommand showApplicationsCommand, ShowProjectPropertiesCommand showProjectPropertiesCommand,
      EventBus eventBus, DeployApplicationPresenter deployAppPresenter)
   {
      // TODO Auto-generated constructor stub
      //      paasAgent.registerPaaS(id, title, image, providesTemplate, supportedProjectTypes, preferencePage);
      //Arrays.asList(ProjectType.JSP, ProjectType.RUBY_ON_RAILS, ProjectType.SPRING, ProjectType.WAR)
      // TODO change hard code types
      JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "Rails", "Spring", "War");
      paasAgent.registerPaaS(ID, ID, resources.cloudFoundry48(), false, requiredProjectTypes, deployAppPresenter, null);
     
      String restContext = "/rest/private";
      new CloudFoundryClientServiceImpl(restContext, new EmptyLoader(), null, eventBus);

      menu.addMenuItem("PaaS/CloudFoudry/Create Application...", createApplicationCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Applications...", showApplicationsCommand);
      menu.addMenuItem("PaaS/CloudFoudry/Switch Account...", loginCommand);
      menu.addMenuItem("Project/Paas/CloudFoudry", showProjectPropertiesCommand);
   }
}