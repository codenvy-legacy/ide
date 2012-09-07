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
package org.exoplatform.ide.security.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Store list of user federated login ,e.g. OpenID, OAuth, etc. Pair userId|password (temporary generated usually)
 * added in this list by suitable service for federated login. After adding userId|password in this list login service
 * should redirect user ot protected area where LoginModule may be used for checking userId|password over method {@link
 * #contains(String, String)}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class FederatedLoginList
{
   public static final Permission LOGIN_LIST_PERMISSION = new RuntimePermission("federatedLoginList");

   private final Set<String> store = new CopyOnWriteArraySet<String>();

   public void add(String userId, String password)
   {
      checkPermission();
      store.add(digest(userId, password));
   }

   public boolean contains(String userId, String password)
   {
      checkPermission();
      return store.contains(digest(userId, password));
   }

   public void remove(String userId, String password)
   {
      checkPermission();
      store.remove(digest(userId, password));
   }

   private void checkPermission()
   {
      SecurityManager security = System.getSecurityManager();
      if (security != null)
      {
         security.checkPermission(LOGIN_LIST_PERMISSION);
      }
   }

   private String digest(String userId, String password)
   {
      MessageDigest messageDigest;
      try
      {
         messageDigest = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      final String src = userId + ':' + password;
      byte[] result = messageDigest.digest(src.getBytes());
      StringBuilder buf = new StringBuilder();
      for (byte b : result)
      {
         buf.append(HEX[(b >> 4) & 0x0f]);
         buf.append(HEX[b & 0x0f]);
      }
      return buf.toString();
   }

   private static final char[] HEX =
      new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
