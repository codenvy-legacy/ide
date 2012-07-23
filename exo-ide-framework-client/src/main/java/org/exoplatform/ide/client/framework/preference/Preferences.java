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
package org.exoplatform.ide.client.framework.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used to preferences registration. Add preferences for any extension: <br>
 * <code>
 *  Preferences.get().addPreferenceItem(new SamplePreference());
 * </code>
 * 
 * Get registered preferences:<br>
 * <code>
 *  Preferences.get().getPreferences();
 * </code>
 * 
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 3:33:38 PM anya $
 * 
 */
public class Preferences
{
   /**
    * List of registered preferences.
    */
   private List<PreferenceItem> preferenceItems;

   /**
    * Instance.
    */
   private static Preferences instance;

   protected Preferences()
   {
      instance = this;
      preferenceItems = new ArrayList<PreferenceItem>();
   }

   /**
    * @return {@link Preferences}
    */
   public static Preferences get()
   {
      if (instance == null)
      {
         instance = new Preferences();
      }
      return instance;
   }

   /**
    * @return {@link List} of {@link PreferenceItem}
    */
   public List<PreferenceItem> getPreferences()
   {
      return preferenceItems;
   }

   /**
    * Registers preference item.
    * 
    * @param preferenceItem
    */
   public void addPreferenceItem(PreferenceItem preferenceItem)
   {
      getPreferences().add(preferenceItem);
   }
}
