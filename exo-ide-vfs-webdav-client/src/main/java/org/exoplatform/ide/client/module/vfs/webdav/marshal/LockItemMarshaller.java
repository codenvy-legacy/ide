/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

public class LockItemMarshaller implements Marshallable
{

   private String userName;

   public LockItemMarshaller(String userName)
   {
      this.userName = userName;
   }

   @Override
   public String marshal()
   {
      String lock = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

      lock += "<D:lockinfo xmlns:D='DAV:'>";
      lock += "<D:lockscope><D:exclusive/></D:lockscope>";
      lock += "<D:locktype><D:write/></D:locktype>";
      lock += "<D:owner>";
      lock += "<D:href>" + userName + "</D:href>";
      lock += "</D:owner>";
      lock += "</D:lockinfo>";

      return lock;
   }
}
