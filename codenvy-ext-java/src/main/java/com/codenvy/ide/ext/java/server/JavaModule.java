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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.ide.ext.java.server.projecttypes.AntSourceFoldersValueProviderFactory;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * @author Evgen Vidolob
 */
@DynaModule
public class JavaModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<ValueProviderFactory> multiBinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
        multiBinder.addBinding().to(AntSourceFoldersValueProviderFactory.class);
        bind(RestNameEnvironment.class);
        bindConstant().annotatedWith(Names.named("project.temp")).to(System.getProperty("java.io.tmpdir"));
    }
}
