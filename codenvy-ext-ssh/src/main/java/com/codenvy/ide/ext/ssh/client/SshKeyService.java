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
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.ssh.shared.GenKeyRequest;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;

/**
 * The client service for working with ssh key.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshService May 18, 2011 4:49:49 PM evgen $
 */
public interface SshKeyService {
    /**
     * Receive all ssh key, stored on server
     *
     * @param callback
     */
    void getAllKeys(@NotNull JsonpAsyncCallback<JavaScriptObject> callback);

    /**
     * Generate new ssh key pare
     *
     * @param host
     *         for ssh key
     * @param callback
     * @throws RequestException
     */
    void generateKey(@NotNull String host, @NotNull AsyncRequestCallback<GenKeyRequest> callback) throws RequestException;

    /**
     * Get public ssh key
     *
     * @param keyItem
     *         to get public key
     * @param callback
     */
    void getPublicKey(@NotNull KeyItem keyItem, @NotNull JsonpAsyncCallback<JavaScriptObject> callback);

    /**
     * Delete ssh key
     *
     * @param keyItem
     *         to delete
     * @param callback
     */
    void deleteKey(@NotNull KeyItem keyItem, @NotNull JsonpAsyncCallback<Void> callback);
}