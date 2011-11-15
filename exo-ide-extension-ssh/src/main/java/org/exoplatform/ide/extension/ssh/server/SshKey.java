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
package org.exoplatform.ide.extension.ssh.server;

/**
 * SSH key.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SshKey
{
   private String path;
   private byte[] bytes;

   public SshKey(String path, byte[] bytes)
   {
      this.path = path;
      this.bytes = bytes;
   }

   protected SshKey()
   {
   }

   /**
    * Identifier of key file, e.g. path to file where key stored.
    * 
    * @return identifier of key file
    */
   public String getIdentifier()
   {
      return path;
   }

   /**
    * Get SSH key as byte array.
    * 
    * @return SSH key as byte array
    */
   public byte[] getBytes()
   {
      return bytes;
   }
}
