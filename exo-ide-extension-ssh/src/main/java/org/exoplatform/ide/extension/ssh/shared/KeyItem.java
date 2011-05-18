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
package org.exoplatform.ide.extension.ssh.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class KeyItem
{
   /** Host name. */
   private String host;
   
   /** URL for download public key. May be <code>null</code> if public key no available. */
   private String publicKeyURL;

   public KeyItem(String host, String publicKeyURL)
   {
      this.host = host;
      this.publicKeyURL = publicKeyURL;
   }

   public KeyItem()
   {
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public String getPublicKeyURL()
   {
      return publicKeyURL;
   }

   public void setPublicKeyURL(String publicKeyURL)
   {
      this.publicKeyURL = publicKeyURL;
   }
}
