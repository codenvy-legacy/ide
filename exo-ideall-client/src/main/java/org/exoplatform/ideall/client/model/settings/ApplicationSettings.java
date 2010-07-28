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
package org.exoplatform.ideall.client.model.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationSettings
{

   private List<String> toolbarItems = new ArrayList<String>();

   private HashMap<String, String> defaultEditors = new HashMap<String, String>();

   private Map<String, String> hotKeys = new HashMap<String, String>();

   private boolean showLineNumbers = true;

   private boolean showOutline = true;

   private String entryPoint;

   public ApplicationSettings()
   {
      toolbarItems.add("");
   }

   public List<String> getToolbarItems()
   {
      return toolbarItems;
   }

   public HashMap<String, String> getDefaultEditors()
   {
      return defaultEditors;
   }

   public Map<String, String> getHotKeys()
   {
      return hotKeys;
   }

   public boolean isShowLineNumbers()
   {
      return showLineNumbers;
   }

   public void setShowLineNumbers(boolean showLineNumbers)
   {
      this.showLineNumbers = showLineNumbers;
   }

   public boolean isShowOutline()
   {
      return showOutline;
   }

   public void setShowOutline(boolean showOutline)
   {
      this.showOutline = showOutline;
   }

   public String getEntryPoint()
   {
      return entryPoint;
   }

   public void setEntryPoint(String entryPoint)
   {
      this.entryPoint = entryPoint;
   }

}
