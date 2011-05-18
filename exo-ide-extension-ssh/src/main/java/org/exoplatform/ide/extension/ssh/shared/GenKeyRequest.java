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
public class GenKeyRequest
{
   /** Remote host name for which generate key. */
   private String host;
   
   /** Comment for public key. */
   private String comment;
   
   /** Passphrase for private key. */
   private String passphrase;

   public GenKeyRequest(String host, String comment, String passphrase)
   {
      this.host = host;
      this.comment = comment;
      this.passphrase = passphrase;
   }

   public GenKeyRequest()
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

   public String getComment()
   {
      return comment;
   }

   public void setComment(String comment)
   {
      this.comment = comment;
   }

   public String getPassphrase()
   {
      return passphrase;
   }

   public void setPassphrase(String passphrase)
   {
      this.passphrase = passphrase;
   }
}
