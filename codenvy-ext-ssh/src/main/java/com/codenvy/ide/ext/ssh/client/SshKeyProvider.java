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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides SSH keys. Must be registered in {@link SshKeyService}:
 * <code>
 * sshKeyService.registerSshKeyProvider(GITHUB_HOST, gitHubSshKeyProvider);
 * </code>
 *
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 */
public interface SshKeyProvider {
    /**
     * @param userId
     *         user's id, for whom to generate key
     * @param callback
     *         calback
     */
    void generateKey(String userId, AsyncCallback<Void> callback);
}
