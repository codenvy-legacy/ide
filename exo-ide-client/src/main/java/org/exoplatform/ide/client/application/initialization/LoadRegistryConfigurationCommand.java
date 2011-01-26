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
package org.exoplatform.ide.client.application.initialization;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.configuration.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.conversation.ConversationServiceImpl;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.module.gadget.service.GadgetServiceImpl;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadRegistryConfigurationCommand implements Command, ConfigurationReceivedSuccessfullyHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private IDEConfiguration applicationConfiguration;

   public LoadRegistryConfigurationCommand(HandlerManager eventBus, IDEConfiguration applicationConfiguration)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   @Override
   public void execute()
   {
      new IDEConfigurationLoader(eventBus, IDELoader.getInstance()).loadConfiguration(applicationConfiguration);
   }

   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      try
      {
         applicationConfiguration = event.getConfiguration();

         new ConversationServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext());

         new TemplateServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getRegistryURL() + "/"
            + RegistryConstants.EXO_APPLICATIONS + "/" + IDEConfigurationLoader.APPLICATION_NAME);

         new GadgetServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext(),
            applicationConfiguration.getGadgetServer(), applicationConfiguration.getPublicContext());

         //         new LoadUserInfoPhase(eventBus, applicationConfiguration, controls);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

}
