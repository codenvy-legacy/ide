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
package org.exoplatform.ide.client.model.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationSettings
{
   
   public enum Store
   {
      
      COOKIES, REGISTRY
      
   }
   
   private HashMap<String, Store> storedIn = new HashMap<String, Store>();
   
   private HashMap<String, Object> values = new HashMap<String, Object>();
   
   public void setValue(String key, Object value) {
      values.put(key, value);
   }
   
   public Object getValue(String key) {
      return values.get(key);
   }
   
   public Store getStoredIn(String key) {
      if (storedIn.get(key) == null) {
         return Store.REGISTRY;
      }
      
      return storedIn.get(key);
   }
   
   public void setStoredIn(String key, Store store) {
      storedIn.put(key, store);
   }
   
   

   private List<String> toolbarItems = new ArrayList<String>();

   private HashMap<String, String> defaultEditors = new HashMap<String, String>();

   private Map<String, String> hotKeys = new HashMap<String, String>();

   private boolean showLineNumbers = true;

   private boolean showOutline = true;

   private String entryPoint;
   
   
   private JSONObject settings;
   
   
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
