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

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ideall.client.model.ApplicationContext;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationContextMarshaller implements Const, Marshallable
{

   private ApplicationContext context;

   public ApplicationContextMarshaller(ApplicationContext context)
   {
      this.context = context;
   }

   public String marshal()
   {
      String xml = "<" + SETTINGS + ">";
      //xml += getLineNumbers();
      xml += getToolbar();
      xml += getEditors();
      xml += "</" + SETTINGS + ">";

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
      for (String toolbarItem : context.getToolBarItems())
      {
         xml += "<" + TOOLBAR_ITEM + ">" + toolbarItem + "</" + TOOLBAR_ITEM + ">";
      }
      xml += "</" + TOOLBAR + ">";

      return xml;
   }

   private String getEditors()
   {
      String xml = "<" + EDITORS + ">";
      for (String key : context.getDefaultEditors().keySet())
      {
         xml += "<" + EDITOR + ">";
         xml += "<" + MIME_TYPE + ">";
         xml += key;
         xml += "</" + MIME_TYPE + ">";
         xml += "<" + EDITOR_DESCRIPTION + ">";
         xml += context.getDefaultEditors().get(key);
         xml += "</" + EDITOR_DESCRIPTION + ">";
         xml += "</" + EDITOR + ">";

      }
      xml += "</" + EDITORS + ">";

      return xml;
   }
}
