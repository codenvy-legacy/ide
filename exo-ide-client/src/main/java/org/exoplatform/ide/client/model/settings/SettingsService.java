/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.model.settings;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;

/**
 * Service to save and to get settings.
 * 
 * Settings can be stored in cookies, registry or both places.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: SettingsService.java Mar 30, 2011 11:06:10 AM vereshchaka $
 *
 */
public abstract class SettingsService
{

   private static SettingsService instance;
   
   protected SettingsService()
   {
      instance = this;
   }
   
   public static SettingsService getInstance()
   {
      return instance;
   }

   /**
    * Save application settings to registry.
    * @param applicationSettings
    * @param callback
    */
   public abstract void saveSettingsToServer(ApplicationSettings applicationSettings,  
      AsyncRequestCallback<ApplicationSettings> callback);
   
   /**
    * Save application settings to cookies.
    * 
    * @param applicationSettings
    */
   public abstract void saveSettingsToCookies(ApplicationSettings applicationSettings);

   /**
    * Restore application settings from cookies.
    * @param applicationSettings
    */
   public abstract void restoreFromCookies(ApplicationSettings applicationSettings);

}
