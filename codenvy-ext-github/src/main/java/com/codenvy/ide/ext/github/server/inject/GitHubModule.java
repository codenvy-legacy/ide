/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2014] Codenvy, S.A. 
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
package com.codenvy.ide.ext.github.server.inject;

import com.codenvy.ide.ext.git.server.nativegit.CredentialsProvider;
import com.codenvy.ide.ext.git.server.nativegit.SshKeyUploaderProvider;
import com.codenvy.ide.ext.github.server.GitHub;
import com.codenvy.ide.ext.github.server.GitHubKeyUploaderProvider;
import com.codenvy.ide.ext.github.server.oauth.GitHubOAuthCredentialProvider;
import com.codenvy.ide.ext.github.server.rest.GitHubExceptionMapper;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * The module that contains configuration of the server side part of the GitHub extension.
 *
 * @author Andrey Plotnikov
 */
@DynaModule
public class GitHubModule extends AbstractModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(GitHub.class);

        Multibinder.newSetBinder(binder(), CredentialsProvider.class).addBinding().to(GitHubOAuthCredentialProvider.class);
        Multibinder.newSetBinder(binder(), SshKeyUploaderProvider.class).addBinding().to(GitHubKeyUploaderProvider.class);

        bind(GitHubExceptionMapper.class).toInstance(new GitHubExceptionMapper());
    }
}
