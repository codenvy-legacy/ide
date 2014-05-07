/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.ssh.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides SSH keys. Must be registered in {@link SshKeyService}:
 * <code>
 *   sshKeyService.registerSshKeyProvider(GITHUB_HOST, gitHubSshKeyProvider);
 * </code>
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 *
 */
public interface SshKeyProvider {
    /**
     * 
     * @param userId user's id, for whom to generate key
     * @param callback calback
     */
    void generateKey(String userId, AsyncCallback<Void> callback);
}
