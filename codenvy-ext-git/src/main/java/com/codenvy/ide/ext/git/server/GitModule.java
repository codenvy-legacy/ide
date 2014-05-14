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
package com.codenvy.ide.ext.git.server;

import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.ide.ext.git.server.nativegit.CredentialsProvider;
import com.codenvy.ide.ext.git.server.nativegit.WSO2OAuthCredentialsProvider;
import com.codenvy.ide.ext.git.server.rest.GitExceptionMapper;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * The module that contains configuration of the server side part of the Git extension.
 *
 * @author andrew00x
 */
@DynaModule
public class GitModule extends AbstractModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), CredentialsProvider.class).addBinding().to(WSO2OAuthCredentialsProvider.class);
        Multibinder.newSetBinder(binder(), ProjectImporter.class).addBinding().to(GitProjectImporter.class);
        bind(GitExceptionMapper.class).toInstance(new GitExceptionMapper());
        bind(GitConfigurationChecker.class).toInstance(new GitConfigurationChecker());
    }
}