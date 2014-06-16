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
package com.codenvy.ide.extension.maven.server.inject;

import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.ide.extension.maven.server.MavenPomReaderService;
import com.codenvy.ide.extension.maven.server.projecttype.MavenArtifactIdValueProviderFactory;
import com.codenvy.ide.extension.maven.server.projecttype.MavenGroupIdValueProviderFactory;
import com.codenvy.ide.extension.maven.server.projecttype.MavenPackagingValueProviderFactory;
import com.codenvy.ide.extension.maven.server.projecttype.MavenProjectTypeDescriptionsExtension;
import com.codenvy.ide.extension.maven.server.projecttype.MavenProjectTypeExtension;
import com.codenvy.ide.extension.maven.server.projecttype.MavenSourceFoldersValueProviderFactory;
import com.codenvy.ide.extension.maven.server.projecttype.MavenVersionValueProviderFactory;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/** @author Artem Zatsarynnyy */
@DynaModule
public class MavenModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MavenProjectTypeExtension.class);
        bind(MavenProjectTypeDescriptionsExtension.class);
        bind(MavenPomReaderService.class);

        Multibinder<ValueProviderFactory> multiBinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
        multiBinder.addBinding().to(MavenSourceFoldersValueProviderFactory.class);
        multiBinder.addBinding().to(MavenArtifactIdValueProviderFactory.class);
        multiBinder.addBinding().to(MavenGroupIdValueProviderFactory.class);
        multiBinder.addBinding().to(MavenVersionValueProviderFactory.class);
        multiBinder.addBinding().to(MavenPackagingValueProviderFactory.class);
    }
}
