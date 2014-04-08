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
package com.codenvy.runner.docker;

import com.codenvy.api.project.server.ValueProviderFactory;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Deploys components that provides project attributes required by the 'docker' based runners.
 *
 * @author andrew00x
 */
public class DockerIdeModule extends AbstractModule {
    @Override
    protected void configure() {
        final Multibinder<ValueProviderFactory> vfMultibinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
        vfMultibinder.addBinding().to(RunnerScriptValueProviderFactory.class);
        vfMultibinder.addBinding().to(RunnerNameValueProviderFactory.class);
    }
}
