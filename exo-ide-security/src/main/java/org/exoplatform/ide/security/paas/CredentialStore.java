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

/**
 * Abstraction of store for user PaaS credential.
 *
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: CredentialStore.java Mar 1, 2013 vetal $
 */
public interface CredentialStore
{
   /**
    * Load user credential for specified target.
    *
    * @param user
    *    user identifier
    * @param target
    *    credential's target, e.g. PaaS name
    * @param credential
    *    instance of Credential to store attributes, see {@link Credential#setAttribute(String, String)}
    * @return <code>true</code> if credential has been found in this store and <code>false</code> if credential not
    *         found in store
    * @throws CredentialStoreException
    */
   boolean load(String user, String target, Credential credential) throws CredentialStoreException;

   /**
    * Save user credential for specified target.
    *
    * @param user
    *    user identifier
    * @param target
    *    credential's target, e.g. PaaS name
    * @param credential
    *    credentials
    * @throws CredentialStoreException
    */
   void save(String user, String target, Credential credential) throws CredentialStoreException;

   /**
    * Delete user credential for specified target.
    *
    * @param user
    *    user identifier
    * @param target
    *    credential's target, e.g. PaaS name
    * @return <code>true</code> if credential successfully removed from this store and <code>false</code> if credential
    *         not found in store
    * @throws CredentialStoreException
    */
   boolean delete(String user, String target) throws CredentialStoreException;
}