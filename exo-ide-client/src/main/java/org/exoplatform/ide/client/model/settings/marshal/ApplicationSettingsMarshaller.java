/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.model.settings.marshal;

import java.util.Iterator;
import java.util.Map.Entry;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ideall.client.model.settings.ApplicationSettings;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsMarshaller implements Const, Marshallable
{

   private ApplicationSettings applicationSettings;

   public ApplicationSettingsMarshaller(ApplicationSettings applicationSettings)
   {
      this.applicationSettings = applicationSettings;
   }

   public String marshal()
   {
      String xml = "<" + SETTINGS + ">";
      //xml += getLineNumbers();
      xml += getToolbar();
      xml += getEditors();
      xml += getHotKeys();
      xml += "</" + SETTINGS + ">";

      return xml;
   }
   
   private String getHotKeys()
   {
      String xml = "<" + HOT_KEYS + ">";
      
      Iterator<Entry<String, String>> it = applicationSettings.getHotKeys().entrySet().iterator();
      while (it.hasNext())
      {
         Entry<String, String> entry = it.next();
         xml += "<" + HOT_KEY + ">";
         xml += "<" + SHORTCUT +">" + entry.getKey() + "</" + SHORTCUT + ">";
         xml += "<" + CONTROL_ID + ">" + entry.getValue() + "</" + CONTROL_ID + ">";
         xml += "</" + HOT_KEY + ">";
      }
      xml += "</" + HOT_KEYS + ">";
      return xml;
   }

//   private String getLineNumbers()
//   {
//      String xml = "<" + LINE_NUMBERS + ">" + context.isShowLineNumbers() + "</" + LINE_NUMBERS + ">";
//      return xml;
//   }

   private String getToolbar()
   {
      String xml = "<" + TOOLBAR + ">";
      for (String toolbarItem : applicationSettings.getToolbarItems())
      {
         xml += "<" + TOOLBAR_ITEM + ">" + toolbarItem + "</" + TOOLBAR_ITEM + ">";
      }
      xml += "</" + TOOLBAR + ">";

      return xml;
   }

   private String getEditors()
   {
      String xml = "<" + EDITORS + ">";
      for (String key : applicationSettings.getDefaultEditors().keySet())
      {
         xml += "<" + EDITOR + ">";
         xml += "<" + MIME_TYPE + ">";
         xml += key;
         xml += "</" + MIME_TYPE + ">";
         xml += "<" + EDITOR_DESCRIPTION + ">";
         xml += applicationSettings.getDefaultEditors().get(key);
         xml += "</" + EDITOR_DESCRIPTION + ">";
         xml += "</" + EDITOR + ">";

      }
      xml += "</" + EDITORS + ">";

      return xml;
   }
}
