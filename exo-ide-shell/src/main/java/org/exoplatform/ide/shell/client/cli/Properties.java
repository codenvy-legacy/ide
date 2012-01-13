/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.shell.client.cli;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

/**
 * Minimal emulation of {@link java.util.Properties}.
 * 
 * @author Hayward Chan
 */
public class Properties extends AbstractMap<Object, Object>
{
   /*
    * Q: Why use a backing map instead of having the class extends from HashMap directly? A: This will make the class
    * serializable. While it works in web mode, it doesn't work in hosted mode because this implementation conflicts with the JDK
    * version, and cause VerifyError. It can be worked around by providing custom field serializer, but it probably isn't worth
    * the effort.
    */
   private final Map<Object, Object> values = new HashMap<Object, Object>();

   public Properties()
   {
   }

   public Enumeration<?> propertyNames()
   {
      return Collections.enumeration(keySet());
   }

   public String getProperty(String name)
   {
      return (String)get(name);
   }

   @Override
   public Object put(Object key, Object value)
   {
      return values.put(key, value);
   }

   @Override
   public Set<Entry<Object, Object>> entrySet()
   {
      return values.entrySet();
   }
}
