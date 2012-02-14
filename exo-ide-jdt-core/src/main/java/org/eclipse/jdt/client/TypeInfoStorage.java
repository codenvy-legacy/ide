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
package org.eclipse.jdt.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;

import org.eclipse.jdt.client.core.Signature;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Local storage for Java types info.
 * Key is FQN of Java Type. Value JSON representation of {@link TypeInfo} class
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 4:31:23 PM evgen $
 */
public class TypeInfoStorage
{

   private static TypeInfoStorage instance;

   private Storage storage;

   protected TypeInfoStorage()
   {
      storage = Storage.getSessionStorageIfSupported();
   }

   public static TypeInfoStorage get()
   {
      if (instance == null)
         instance = new TypeInfoStorage();
      return instance;
   }

   public void putType(String key, String type)
   {
      storage.setItem(key, type);
   }

   public String getType(String key)
   {
      return storage.getItem(key);
   }

   public boolean containsKey(String key)
   {
      return storage.getItem(key) != null;
   }

   public List<JSONObject> getTypesByNamePrefix(String prefix, boolean fqnPart)
   {
      List<JSONObject> res = new ArrayList<JSONObject>();
      for (int i = 0; i < storage.getLength(); i++)
      {
         String key = storage.key(i);
         if (fqnPart && !key.startsWith(prefix))
         {
            continue;
         }
         else
         {
            String simpleName = Signature.getSimpleName(key);
            if (simpleName.equals(key) || !simpleName.startsWith(prefix))
               continue;
         }
         res.add(JSONParser.parseLenient(storage.getItem(key)).isObject());
      }
      return res;

   }
}
