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

import com.codenvy.ide.ext.git.server.nativegit.CredentialsProvider;
import com.codenvy.ide.ext.git.server.nativegit.OAuthCredentialsProvider;
import com.codenvy.ide.ext.git.server.provider.GitVendorService;
import com.codenvy.ide.ext.git.server.provider.rest.ProviderExceptionMapper;
import com.codenvy.ide.ext.git.server.provider.rest.ProviderService;
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
        Multibinder<CredentialsProvider> multiBindings = Multibinder.newSetBinder(binder(), CredentialsProvider.class);
        multiBindings.addBinding().to(OAuthCredentialsProvider.class);

        Multibinder<GitVendorService> gitVendorServices = Multibinder.newSetBinder(binder(), GitVendorService.class);

        bind(ProviderService.class);
        bind(ProviderExceptionMapper.class).toInstance(new ProviderExceptionMapper());
    }
}