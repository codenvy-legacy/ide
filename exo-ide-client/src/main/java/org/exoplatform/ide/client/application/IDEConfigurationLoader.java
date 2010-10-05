/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.menu.event.UpdateMainMenuEvent;
import org.exoplatform.gwtframework.ui.client.component.statusbar.event.UpdateStatusBarEvent;
import org.exoplatform.gwtframework.ui.client.component.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.GetApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.userinfo.event.GetUserInfoEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.configuration.Configuration;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.conversation.ConversationServiceImpl;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.module.gadget.service.GadgetServiceImpl;
import org.exoplatform.ide.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class IDEConfigurationLoader implements ConfigurationReceivedSuccessfullyHandler, UserInfoReceivedHandler,
   ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private ApplicationConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   private List<String> toolbarDefaultItems;

   private List<String> statusBarItems;

   public IDEConfigurationLoader(HandlerManager eventBus, ApplicationContext context, List<Control> controls,
      List<String> toolbarDefaultItems, List<String> statusBarItems)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.controls = controls;
      this.toolbarDefaultItems = toolbarDefaultItems;
      this.statusBarItems = statusBarItems;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      handlers.addHandler(UserInfoReceivedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      new Configuration(eventBus, IDELoader.getInstance());
   }

   /**
    * Called in case the valid configuration of the application is received
    * 
    * @see org.exoplatform.ide.client.model.configuration.ApplicationConfigurationReceivedHandler#onApplicationConfigurationReceived(org.exoplatform.ide.client.model.configuration.ApplicationConfigurationReceivedEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      try
      {
         applicationConfiguration = event.getConfiguration();

         new ConversationServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext());

         new TemplateServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getRegistryURL() + "/"
            + RegistryConstants.EXO_APPLICATIONS + "/" + Configuration.APPLICATION_NAME);

         new GadgetServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext(),
            applicationConfiguration.getGadgetServer(), applicationConfiguration.getPublicContext());

         new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new GetUserInfoEvent());
            }
         }.schedule(10);

      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Called when user information ( name, ect ) is received
    * 
    * @see org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedEvent)
    */
   public void onUserInfoReceived(final UserInfoReceivedEvent event)
   {
      new Timer()
      {
         @Override
         public void run()
         {
            new ControlsFormatter(eventBus).format(controls);
            eventBus.fireEvent(new ControlsUpdatedEvent(controls));

            new SettingsService(eventBus, applicationConfiguration.getRegistryURL(), event.getUserInfo().getName(),
               IDELoader.getInstance());
            eventBus.fireEvent(new GetApplicationSettingsEvent());
         }
      }.schedule(10);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      /*
       * verify entry point
       */
      if (applicationSettings.getValueAsString("entry-point") == null)
      {
         if (applicationConfiguration.getDefaultEntryPoint() != null)
         {
            String defaultEntryPoint = applicationConfiguration.getDefaultEntryPoint();
            if (!defaultEntryPoint.endsWith("/"))
            {
               defaultEntryPoint += "/";
            }
   
            applicationSettings.setValue("entry-point", applicationConfiguration.getDefaultEntryPoint(), Store.COOKIES);
         }
      }

      /*
       * verify toolbar items
       */

      applicationSettings.setValue("toolbar-default-items", toolbarDefaultItems, Store.NONE);
      if (applicationSettings.getValueAsList("toolbar-items") == null)
      {
         List<String> toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(toolbarDefaultItems);
         applicationSettings.setValue("toolbar-items", toolbarItems, Store.REGISTRY);
      }

      new Timer()
      {
         @Override
         public void run()
         {
            initServices();
         }
      }.schedule(10);
   }

   private void initializeApplication()
   {
      new Timer()
      {
         @Override
         public void run()
         {
            eventBus.fireEvent(new RegisterEventHandlersEvent());

            new Timer()
            {
               @Override
               public void run()
               {
                  try
                  {
                     Map<String, File> openedFiles = new LinkedHashMap<String, File>();
                     eventBus.fireEvent(new InitializeApplicationEvent(openedFiles, null));
                  }
                  catch (Throwable e)
                  {
                     e.printStackTrace();
                  }
               }

            }.schedule(10);

         }
      }.schedule(10);
   }

   private void initServices()
   {
      eventBus.fireEvent(new InitializeServicesEvent(applicationConfiguration, IDELoader.getInstance()));

      new Timer()
      {
         @Override
         public void run()
         {
            initialize();
         }
      }.schedule(10);
   }

   private void initialize()
   {
      /*
       * Updating top menu
       */
      eventBus.fireEvent(new UpdateMainMenuEvent(controls));
      eventBus.fireEvent(new UpdateStatusBarEvent(context.getStatusBarItems(), controls));

      List<String> toolbarItems = applicationSettings.getValueAsList("toolbar-items");
      if (toolbarItems == null)
      {
         toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(toolbarDefaultItems);
      }

      eventBus.fireEvent(new UpdateToolbarEvent(toolbarItems, controls));
      eventBus.fireEvent(new UpdateStatusBarEvent(statusBarItems, controls));

      initializeApplication();      
      
      if (applicationSettings.getValueAsString("entry-point") != null)
      {
         String entryPoint = applicationSettings.getValueAsString("entry-point");
         eventBus.fireEvent(new EntryPointChangedEvent(entryPoint));
         new WorkspaceChecker(eventBus, entryPoint, applicationSettings);
      }
      else
      {
         // TODO [IDE-307] handle incorrect appConfig["entryPoint"] property value
         Dialogs
            .getInstance()
            .showError(
               "Workspace was not set!",
               "Workspace was not set. Please, click on 'Ok' button and select another workspace manually from the next dialog!",
               new BooleanValueReceivedCallback()
               {
                  public void execute(Boolean value)
                  {                     
                     if (value)
                     {
                        eventBus.fireEvent(new SelectWorkspaceEvent());
                     }
                  }
               });
      }
   }

}
