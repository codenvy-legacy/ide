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
package org.exoplatform.ide.extension.ssh.server;

import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SshKeyStore {
    /**
     * Add prepared private key.
     * 
     * @param host host name
     * @param key private key as byte array
     * @throws SshKeyStoreException if any error occurs in key store
     */
    void addPrivateKey(String host, byte[] key) throws SshKeyStoreException;

    /**
     * Get SSH private key for <code>host</code>.
     * 
     * @param host host name
     * @return private key
     * @throws SshKeyStoreException if any error occurs in key store
     */
    SshKey getPrivateKey(String host) throws SshKeyStoreException;

    /**
     * Get SSH public key for <code>host</code>. Obtained key should be copied to remote host. Typically this method should be used after
     * generated key-pair with method {@link #genKeyPair(String, String, String)}.
     * 
     * @param host host name
     * @return public key
     * @throws SshKeyStoreException if any error occurs in key store
     */
    SshKey getPublicKey(String host) throws SshKeyStoreException;

    /**
     * Generate SSH key files.
     * 
     * @param host host name
     * @param comment comment to add in public key
     * @param passPhrase optional pass-phrase to protect private key
     * @throws SshKeyStoreException if any error occurs in key store
     */
    void genKeyPair(String host, String comment, String passPhrase) throws SshKeyStoreException;

    /**
     * Generate SSH key files.
     * 
     * @param host host name
     * @param comment comment to add in public key
     * @param passPhrase optional pass-phrase to protect private key
     * @param keyMail optional email for generated key
     * @throws SshKeyStoreException if any error occurs in key store
     */
    void genKeyPair(String host, String comment, String passPhrase, String keyMail) throws SshKeyStoreException;

    /**
     * Remove both private and public (if any) keys.
     * 
     * @param host host name
     * @throws SshKeyStoreException if any error occurs in key store
     */
    void removeKeys(String host) throws SshKeyStoreException;

    /**
     * Get list of hosts for which keys are available.
     * 
     * @return list of hosts. Even there is no keys for any host empty set returned never <code>null</code>
     * @throws SshKeyStoreException if any error occurs in key store
     */
    Set<String> getAll() throws SshKeyStoreException;
}
