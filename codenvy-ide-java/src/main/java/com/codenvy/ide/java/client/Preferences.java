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
package com.codenvy.ide.java.client;

import com.google.gwt.storage.client.Storage;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:30:50 PM Mar 29, 2012 evgen $
 * 
 */
public class Preferences
{

   /**
    * A named preference that stores the content assist LRU history
    * <p>
    * Value is an JSON encoded version of the history.
    * </p>
    * 
    */
   public static final String CODEASSIST_LRU_HISTORY = "content_assist_lru_history_";

   public static final String QUALIFIED_TYPE_NAMEHISTORY = "Qualified_Type_Name_History_";

   private Storage storage;

   private boolean supported = false;

   /**
    * 
    */
   public Preferences()
   {
      if (Storage.isSupported())
      {
         storage = Storage.getLocalStorageIfSupported();
         supported = true;
      }

   }

   /**
    * @param key
    * @param string
    */
   public void setValue(String key, String string)
   {
      if (supported)
         storage.setItem(key, string);
   }

   /**
    * @param key
    * @return
    */
   public String getString(String key)
   {
      if (supported)
         return storage.getItem(key);
      return "";
   }

}
