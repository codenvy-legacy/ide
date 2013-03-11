/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation.handler;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.Settings;
import org.exoplatform.ide.client.navigation.control.ShowHideHiddenFilesControl;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideHiddenFilesCommandHandler.java Apr 2, 2012 10:27:21 AM azatsarynnyy $
 *
 */
public class ShowHideHiddenFilesCommandHandler implements ShowHideHiddenFilesHandler, InitializeServicesHandler,
   ApplicationSettingsReceivedHandler
{
   /**
    * Stores the pattern for hidden files.
    */
   private String hiddenFilesPattern = "";

   private ApplicationSettings applicationSettings;

   public ShowHideHiddenFilesCommandHandler()
   {
      IDE.getInstance().addControl(new ShowHideHiddenFilesControl());

      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      hiddenFilesPattern = event.getApplicationConfiguration().getHiddenFiles();

      boolean showHiddenFiles = applicationSettings.getValueAsBoolean(Settings.SHOW_HIDDEN_FILES);
      IDE.fireEvent(new ShowHideHiddenFilesEvent(showHiddenFiles));
   }

   /**
    * @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent)
    */
   @Override
   public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event)
   {
      if (event.isFilesShown())
      {
         DirectoryFilter.get().setPattern("");
      }
      else
      {
         DirectoryFilter.get().setPattern(hiddenFilesPattern);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsBoolean(Settings.SHOW_HIDDEN_FILES) == null)
      {
         applicationSettings.setValue(Settings.SHOW_HIDDEN_FILES, Boolean.FALSE, Store.COOKIES);
      }
   }
}
