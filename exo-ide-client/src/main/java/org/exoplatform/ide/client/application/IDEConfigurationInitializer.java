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
package org.exoplatform.ide.client.application;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.command.ui.SetToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.application.phases.RestoreOpenedFilesPhase;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.discovery.event.IsDiscoverableResultReceivedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.model.configuration.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.configuration.IDEInitializationConfiguration;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.model.settings.SettingsServiceImpl;
import org.exoplatform.ide.client.workspace.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 *
 */
public class IDEConfigurationInitializer implements ApplicationSettingsReceivedHandler, VfsChangedHandler
{
   private final HandlerManager eventBus;

   private IDEConfiguration applicationConfiguration;

   private ControlsRegistration controls;

   private ApplicationSettings applicationSettings;

   private HandlerRegistration handler;

   /**
    * @param controls
    */
   public IDEConfigurationInitializer(ControlsRegistration controls)
   {
      super();
      this.controls = controls;
      eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handler = eventBus.addHandler(VfsChangedEvent.TYPE, this);
   }

   public void loadConfiguration()
   {
      new IDEConfigurationLoader(eventBus, IDELoader.getInstance())
         .loadConfiguration(new AsyncRequestCallback<IDEInitializationConfiguration>()
         {

            @Override
            protected void onSuccess(IDEInitializationConfiguration result)
            {
               try
               {
                  applicationConfiguration = result.getIdeConfiguration();
                  applicationSettings = result.getSettings();

                  eventBus.fireEvent(new ConfigurationReceivedSuccessfullyEvent(applicationConfiguration));

                  new SettingsServiceImpl(eventBus, applicationConfiguration.getRegistryURL(), result.getUserInfo()
                     .getName(), IDELoader.getInstance(), applicationConfiguration.getContext());

                  SettingsService.getInstance().restoreFromCookies(applicationSettings);

                  if (result.getUserInfo().getRoles() != null && result.getUserInfo().getRoles().size() > 0)
                  {
                     controls.initControls(result.getUserInfo().getRoles());
                     eventBus.fireEvent(new ApplicationSettingsReceivedEvent(result.getSettings()));
                     eventBus.fireEvent(new IsDiscoverableResultReceivedEvent(result.isDiscoverable()));
                     eventBus.fireEvent(new UserInfoReceivedEvent(result.getUserInfo()));
                     checkEntryPoint();
                  }
                  else
                  {
                     Dialogs.getInstance().showError(org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.userHasNoRoles());
                  }
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
         });
   }

   private void checkEntryPoint()
   {
      /*
       * verify entry point
       */
      if (applicationSettings.getValueAsString("entry-point") == null)
      {
         if (applicationConfiguration.getVfsId() != null)
         {
            applicationSettings.setValue("entry-point", applicationConfiguration.getVfsId(), Store.COOKIES);
         }
      }

      if (applicationSettings.getValueAsString("entry-point") != null)
      {
         String entryPoint = applicationSettings.getValueAsString("entry-point");
         eventBus.fireEvent(new SwitchVFSEvent(entryPoint));
      }
      else
      {
         promptToSelectEntryPoint();
      }
   }

   public void onVfsChanged(VfsChangedEvent event)
   {
      //      removeHandlers();
      if (handler != null)
         handler.removeHandler();

      if (event.getVfsInfo() == null || event.getVfsInfo().getId() == null)
      {
         promptToSelectEntryPoint();
      }
      else
      {
         new RestoreOpenedFilesPhase(eventBus, applicationSettings);
      }
   }

   protected void promptToSelectEntryPoint()
   {
      // TODO [IDE-307] handle incorrect appConfig["entryPoint"] property value
      Dialogs.getInstance().showError(org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.confWorkspaceWasNotSetTitle(),
         org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.confWorkspaceWasNotSetText(),
         new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value)
               {
                  eventBus.fireEvent(new SelectWorkspaceEvent());
               }
            }
         });
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {

      /*
       * verify toolbar items
       */

      applicationSettings.setValue("toolbar-default-items", controls.getToolbarDefaultControls(), Store.NONE);
      if (applicationSettings.getValueAsList("toolbar-items") == null)
      {
         List<String> toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(controls.getToolbarDefaultControls());
         applicationSettings.setValue("toolbar-items", toolbarItems, Store.SERVER);
      }

      initServices();
   }

   private void initServices()
   {
      eventBus.fireEvent(new InitializeServicesEvent(applicationConfiguration, IDELoader.getInstance()));

      /*
       * Updating top menu
       */
      eventBus.fireEvent(new RefreshMenuEvent());

      List<String> toolbarItems = applicationSettings.getValueAsList("toolbar-items");
      if (toolbarItems == null)
      {
         toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(controls.getToolbarDefaultControls());
      }

      eventBus.fireEvent(new SetToolbarItemsEvent("exoIDEToolbar", toolbarItems, controls.getRegisteredControls()));
      eventBus.fireEvent(new SetToolbarItemsEvent("exoIDEStatusbar", controls.getStatusBarControls(), controls
         .getRegisteredControls()));
   }


}
