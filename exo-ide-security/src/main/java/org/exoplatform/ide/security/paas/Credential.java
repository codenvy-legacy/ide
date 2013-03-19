/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.security.paas;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Credentials.java Mar 1, 2013 vetal $
 */
public class Credential
{
   private final Map<String, String> attributes;

   public Credential()
   {
      attributes = new HashMap<String, String>(2);
   }

   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   public String getAttribute(String name)
   {
      return attributes.get(name);
   }

   public void setAttribute(String name, String value)
   {
      attributes.put(name, value);
   }
}

