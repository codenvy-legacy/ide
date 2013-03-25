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

import com.codenvy.ide.commons.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DummyCredentialStore implements CredentialStore
{
   private final Map<String, Pair[]> myCredentials = new HashMap<String, Pair[]>();
   private final Lock lock = new ReentrantLock();

   @Override
   public boolean load(String user, String target, Credential credential) throws CredentialStoreException
   {
      lock.lock();
      try
      {
         Pair[] persistentCredential = myCredentials.get(user + target);
         if (persistentCredential == null)
         {
            return false;
         }
         for (Pair attribute : persistentCredential)
         {
            credential.setAttribute(attribute.getName(), attribute.getValue());
         }
         return true;
      }
      finally
      {
         lock.unlock();
      }
   }

   @Override
   public void save(String user, String target, Credential credential) throws CredentialStoreException
   {
      lock.lock();
      try
      {
         final Map<String, String> attributes = credential.getAttributes();
         Pair[] persistentCredential = new Pair[attributes.size()];
         int i = 0;
         for (Map.Entry<String, String> e : attributes.entrySet())
         {
            persistentCredential[i++] = new Pair(e.getKey(), e.getValue());
         }
         myCredentials.put(user + target, persistentCredential);
      }
      finally
      {
         lock.unlock();
      }

   }

   @Override
   public boolean delete(String user, String target) throws CredentialStoreException
   {
      lock.lock();
      try
      {
         return myCredentials.remove(user + target) != null;
      }
      finally
      {
         lock.unlock();
      }
   }
}
