/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.ssh.server;

import java.util.Set;

/** @author andrew00x */
public interface SshKeyStore {
    /**
     * Add prepared private key.
     *
     * @param host
     *         host name
     * @param key
     *         private key as byte array
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    void addPrivateKey(String host, byte[] key) throws SshKeyStoreException;

    /**
     * Get SSH private key for <code>host</code>.
     *
     * @param host
     *         host name
     * @return private key
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    SshKey getPrivateKey(String host) throws SshKeyStoreException;

    /**
     * Get SSH public key for <code>host</code>. Obtained key should be copied to remote host. Typically this method should be used after
     * generated key-pair with method {@link #genKeyPair(String, String, String)}.
     *
     * @param host
     *         host name
     * @return public key
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    SshKey getPublicKey(String host) throws SshKeyStoreException;

    /**
     * Generate SSH key files.
     *
     * @param host
     *         host name
     * @param comment
     *         comment to add in public key
     * @param passPhrase
     *         optional pass-phrase to protect private key
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    SshKeyPair genKeyPair(String host, String comment, String passPhrase) throws SshKeyStoreException;

    /**
     * Generate SSH key files.
     *
     * @param host
     *         host name
     * @param comment
     *         comment to add in public key
     * @param passPhrase
     *         optional pass-phrase to protect private key
     * @param keyMail
     *         optional email for generated key
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    SshKeyPair genKeyPair(String host, String comment, String passPhrase, String keyMail) throws SshKeyStoreException;

    /**
     * Remove both private and public (if any) keys.
     *
     * @param host
     *         host name
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    void removeKeys(String host) throws SshKeyStoreException;

    /**
     * Get list of hosts for which keys are available.
     *
     * @return list of hosts. Even there is no keys for any host empty set returned never <code>null</code>
     * @throws SshKeyStoreException
     *         if any error occurs in key store
     */
    Set<String> getAll() throws SshKeyStoreException;
}
