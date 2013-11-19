/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.GitUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Native implementation for GitConnectionFactory
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitConnectionFactory extends GitConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NativeGitConnectionFactory.class);
    private final SshKeysManager keysManager;
    private CredentialsLoader credentialsLoader;

    public NativeGitConnectionFactory(SshKeysManager keysManager, CredentialsLoader credentialsLoader) {
        this.keysManager = keysManager;
        this.credentialsLoader = credentialsLoader;
    }

    public NativeGitConnectionFactory(SshKeysManager keysManager) {
        this.keysManager = keysManager;
        LOG.debug("No instance of OAuthTokenProvider was found.");
    }

    /**
     * @see org.exoplatform.ide.git.server.GitConnectionFactory#getConnection(java.io.File, com.codenvy.ide.ext.git.shared_.GitUser)
     */
    public GitConnection getConnection(File workDir, GitUser user) throws GitException {
        return new NativeGitConnection(workDir, user, keysManager, credentialsLoader);
    }

}
