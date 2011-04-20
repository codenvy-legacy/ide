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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.CredentialItem.StringType;

import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class CredentialsProviderImpl extends CredentialsProvider
{
   private String username;
   private char[] password;
   private Map<String, String> passphrases;

   CredentialsProviderImpl(String username, String password, Map<String, String> passphrases)
   {
      this(username, password != null ? password.toCharArray() : null, passphrases);
   }

   CredentialsProviderImpl(String username, char[] password, Map<String, String> passphrases)
   {
      this.username = username;
      this.password = password;
      this.passphrases = passphrases;
   }

   /**
    * @see org.eclipse.jgit.transport.CredentialsProvider#isInteractive()
    */
   @Override
   public boolean isInteractive()
   {
      return false;
   }

   /**
    * @see org.eclipse.jgit.transport.CredentialsProvider#supports(org.eclipse.jgit.transport.CredentialItem[])
    */
   @Override
   public boolean supports(CredentialItem... items)
   {
      for (int i = 0; i < items.length; i++)
      {
         if (items[i] instanceof CredentialItem.Username)
            continue;
         else if (items[i] instanceof CredentialItem.Password)
            continue;
         else if (items[i] instanceof CredentialItem.StringType)
            continue;
         else
            return false;
      }
      return true;
   }

   /**
    * @see org.eclipse.jgit.transport.CredentialsProvider#get(org.eclipse.jgit.transport.URIish,
    *      org.eclipse.jgit.transport.CredentialItem[])
    */
   @Override
   public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem
   {
      for (int i = 0; i < items.length; i++) {
         if (items[i] instanceof CredentialItem.Username)
            ((CredentialItem.Username) items[i]).setValue(username);
         else if (items[i] instanceof CredentialItem.Password)
            ((CredentialItem.Password) items[i]).setValue(password);
         else if (items[i] instanceof CredentialItem.StringType && passphrases != null)
            ((StringType)items[i]).setValue(passphrases.get(uri.getUser() + "@" + uri.getHost()));
         else
            throw new UnsupportedCredentialItem(uri, items[i].getPromptText());
      }
      return true;
   }
}
