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

import org.apache.commons.lang.Validate;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationSettings
{

   public enum Store {

      COOKIES, REGISTRY

   }

   private HashMap<String, Store> storedIn = new HashMap<String, Store>();

   /*
    * Values can be only next types: String, Integer, Boolean, Map, List
    */
   private HashMap<String, Object> values = new HashMap<String, Object>();
   
   public Map<String, Object> getValues() {
      return values;
   }

   public void setValue(String key, Object value, Store store)
   {
      values.put(key, value);
      storedIn.put(key, store);
   }

   public Object getValue(String key)
   {
      return values.get(key);
   }
   
   public String getStringValue(String key) {
      return (String)values.get(key);
   }
   
   public Integer getIntValue(String key) {
      if (values.get(key) == null) {
         return 0;
      }
      
      return new Integer((String)values.get(key));
   }
   
   
   public Store getStoredIn(String key)
   {
      if (storedIn.get(key) == null)
      {
         return Store.REGISTRY;
      }

      return storedIn.get(key);
   }

//   public void setStoredIn(String key, Store store)
//   {
//      storedIn.put(key, store);
//   }

   private List<String> toolbarItems = new ArrayList<String>();

//   private HashMap<String, String> defaultEditors = new HashMap<String, String>();

   //   private boolean showLineNumbers = true;

//   private String entryPoint;

   public ApplicationSettings()
   {
      toolbarItems.add("");
   }

   public List<String> getToolbarItems()
   {
      return toolbarItems;
   }

//   public HashMap<String, String> getDefaultEditors()
//   {
//      return defaultEditors;
//   }

   //   public boolean isShowLineNumbers()
   //   {
   //      return showLineNumbers;
   //   }
   //
   //   public void setShowLineNumbers(boolean showLineNumbers)
   //   {
   //      this.showLineNumbers = showLineNumbers;
   //   }

//   public String getEntryPoint()
//   {
//      return entryPoint;
//   }

//   public void setEntryPoint(String entryPoint)
//   {
//      this.entryPoint = entryPoint;
//   }

}
