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
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.ssh.dto.GenKeyRequest;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.ext.ssh.dto.PublicKey;
import com.codenvy.ide.rest.AsyncRequestCallback;

import javax.validation.constraints.NotNull;

/**
 * The client service for working with ssh key.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 */
public interface SshKeyService {

    /**
     * Get the list of SSH keys tokenProvider.
     *
     * @return {@link com.codenvy.ide.collections.StringMap}
     */
    StringMap<SshKeyProvider> getSshKeyProviders();

    /**
     * Register SSH key provider
     *
     * @param host
     *         host, for which to provide keys
     * @param sshKeyProvider
     *         keys provider
     */
    void registerSshKeyProvider(@NotNull String host, @NotNull SshKeyProvider sshKeyProvider);

    /**
     * Receive all ssh key, stored on server
     *
     * @param callback
     */
    void getAllKeys(@NotNull AsyncRequestCallback<Array<KeyItem>> callback);

    /**
     * Generate new ssh key pare
     *
     * @param host
     *         for ssh key
     * @param callback
     */
    void generateKey(@NotNull String host, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Get public ssh key
     *
     * @param keyItem
     *         to get public key
     * @param callback
     */
    void getPublicKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<PublicKey> callback);

    /**
     * Delete ssh key
     *
     * @param keyItem
     *         to delete
     * @param callback
     */
    void deleteKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<Void> callback);
}