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
package org.exoplatform.ide.client.framework.settings;

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

   public enum Store {

      COOKIES, REGISTRY, NONE

   }

   private HashMap<String, Store> stores = new HashMap<String, Store>();

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
      stores.put(key, store);
   }

   public Object getValueAsObject(String key)
   {
      return values.get(key);
   }
   
   public String getValueAsString(String key) {
      return (String)values.get(key);
   }
   
   public Integer getValueAsInteger(String key) {
      if (values.get(key) == null) {
         return 0;
      }
      
      return new Integer((String)values.get(key));
   }
   
   public Boolean getValueAsBoolean(String key) {
      if (values.get(key) == null) {
         return null;
      }
      
      return (Boolean)values.get(key);
   }
   
   @SuppressWarnings("unchecked")
   public List<String> getValueAsList(String key) {
      if (values.get(key) == null) {
         return null;
      }
      
      return (List<String>)(values.get(key));      
   }
   
   @SuppressWarnings("unchecked")
   public Map<String, String> getValueAsMap(String key) {
      if (values.get(key) == null) {
         return null;
      }
      
      return (Map<String, String>)(values.get(key));
   }
   
   public Store getStore(String key)
   {
      if (stores.get(key) == null)
      {
         return Store.NONE;
      }

      return stores.get(key);
   }

}
